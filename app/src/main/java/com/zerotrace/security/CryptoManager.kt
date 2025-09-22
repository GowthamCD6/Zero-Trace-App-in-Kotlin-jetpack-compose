package com.zerotrace.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.util.Base64
import com.zerotrace.data.models.WipeData
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.kernel.pdf.PdfDocument
import java.io.ByteArrayOutputStream
import org.json.JSONObject

object CryptoManager {
    private const val KEY_ALIAS = "ZeroTraceKey"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    fun signJson(wipeData: WipeData, context: Context): String {
        val json = JSONObject()
        json.put("wipeId", wipeData.wipeId)
        json.put("manufacturer", wipeData.deviceInfo.manufacturer)
        json.put("model", wipeData.deviceInfo.model)
        json.put("androidVersion", wipeData.deviceInfo.androidVersion)
        json.put("serial", wipeData.deviceInfo.serial)
        json.put("time", wipeData.deviceInfo.time)
        val jsonString = json.toString()
        val signature = signData(jsonString.toByteArray())
        val payload = JSONObject()
        payload.put("wipeId", wipeData.wipeId)
        payload.put("json", jsonString)
        payload.put("signature", signature)
        return payload.toString()
    }

    private fun signData(data: ByteArray): String {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
            val spec = KeyGenParameterSpec.Builder(KEY_ALIAS,
                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setKeySize(2048)
                .build()
            kpg.initialize(spec)
            kpg.generateKeyPair()
        }
        val privateKey = keyStore.getKey(KEY_ALIAS, null)
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey as java.security.PrivateKey)
        signature.update(data)
        val signed = signature.sign()
        return Base64.getEncoder().encodeToString(signed)
    }

    fun verifySignature(json: String, signatureStr: String): Boolean {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        if (!keyStore.containsAlias(KEY_ALIAS)) return false
        val publicKey = keyStore.getCertificate(KEY_ALIAS).publicKey
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(json.toByteArray())
        val signatureBytes = Base64.getDecoder().decode(signatureStr)
        return signature.verify(signatureBytes)
    }

    fun generatePdfCertificate(context: Context, wipeId: String): ByteArray {
        val baos = ByteArrayOutputStream()
        val pdfWriter = PdfWriter(baos)
        val pdfDoc = PdfDocument(pdfWriter)
        val document = Document(pdfDoc)
        document.add(Paragraph("ZeroTrace Wipe Certificate"))
        document.add(Paragraph("Wipe ID: $wipeId"))
        document.add(Paragraph("Date: ${System.currentTimeMillis()}"))
        document.add(Paragraph("Device wiped securely as per NIST SP 800-88."))
        document.close()
        return baos.toByteArray()
    }
}
