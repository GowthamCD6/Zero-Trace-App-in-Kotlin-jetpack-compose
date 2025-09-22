plugins {
    id("com.android.application") version "8.2.0" apply false
    kotlin("android") version "1.9.21" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    }
}
