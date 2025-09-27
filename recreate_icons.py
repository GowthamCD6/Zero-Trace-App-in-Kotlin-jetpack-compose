#!/usr/bin/env python3
"""
Script to recreate valid PNG launcher icons for ZeroTrace app
"""

import os
from PIL import Image, ImageDraw, ImageFont
import io

def create_launcher_icon(size, output_path):
    """Create a launcher icon with given size"""
    # Create a new image with transparent background
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Create circular background
    circle_color = (33, 150, 243, 255)  # Blue color
    draw.ellipse([0, 0, size, size], fill=circle_color)
    
    # Add text "ZT" for ZeroTrace
    try:
        # Try to use a default font
        font_size = size // 3
        font = ImageFont.load_default()
    except:
        font = None
    
    # Draw text
    text = "ZT"
    if font:
        bbox = draw.textbbox((0, 0), text, font=font)
        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]
        x = (size - text_width) // 2
        y = (size - text_height) // 2
        draw.text((x, y), text, fill=(255, 255, 255, 255), font=font)
    else:
        # Fallback without font
        text_size = size // 4
        x = size // 2 - text_size
        y = size // 2 - text_size // 2
        draw.text((x, y), text, fill=(255, 255, 255, 255))
    
    # Save the image
    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    img.save(output_path, 'PNG', optimize=True)
    print(f"Created icon: {output_path} ({size}x{size})")

def main():
    """Main function to create all launcher icons"""
    base_path = "app/src/main/res"
    
    # Icon sizes for different densities
    icon_sizes = {
        'mipmap-mdpi': 48,
        'mipmap-hdpi': 72,
        'mipmap-xhdpi': 96,
        'mipmap-xxhdpi': 144,
        'mipmap-xxxhdpi': 192
    }
    
    for folder, size in icon_sizes.items():
        icon_path = os.path.join(base_path, folder, 'ic_launcher.png')
        create_launcher_icon(size, icon_path)
    
    print("All launcher icons created successfully!")

if __name__ == "__main__":
    main()