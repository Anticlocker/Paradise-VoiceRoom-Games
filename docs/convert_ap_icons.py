import os
import glob
import re
import shutil
import xml.etree.ElementTree as ET

src_dir = 'docs/aurora_paradise_kit/Aurora_Paradise_Theme_Kit/assets'
assets_dest = 'app/src/main/assets/aurora_paradise'
res_dest = 'app/src/main/res/drawable'

os.makedirs(assets_dest, exist_ok=True)
os.makedirs(res_dest, exist_ok=True)

files = glob.glob(os.path.join(src_dir, '*.svg'))
print(f"Processing {len(files)} Aurora Paradise SVG icons...")

for f in files:
    fname = os.path.basename(f)
    shutil.copyfile(f, os.path.join(assets_dest, fname))
    
    with open(f, 'r', encoding='utf-8') as fp:
        raw = fp.read()
    
    name, _ = os.path.splitext(fname)
    xml_name = f"ap_ic_{name}.xml"
    xml_path = os.path.join(res_dest, xml_name)
    
    # Extract gradient stops
    stops = re.findall(r'stop-color="([^"]+)"', raw)
    start_color = stops[0] if len(stops) > 0 else "#FF9C86"
    end_color = stops[1] if len(stops) > 1 else start_color
    
    # Extract path data d="..."
    path_match = re.search(r'<path\s+d="([^"]+)"', raw)
    path_d = path_match.group(1) if path_match else ""
    
    # Extract rect rx and fill
    rect_fill = "#FFF9F5"
    
    # Rounded rect path for 96x96 with rx=25 at (2,2)
    # Total viewport is 100x100
    rect_path = "M 27 2 h 46 a 25 25 0 0 1 25 25 v 46 a 25 25 0 0 1 -25 25 h -46 a 25 25 0 0 1 -25 -25 v -46 a 25 25 0 0 1 25 -25 Z"
    
    xml_content = f"""<vector xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:aapt="http://schemas.android.com/aapt"
    android:width="100dp"
    android:height="100dp"
    android:viewportWidth="100"
    android:viewportHeight="100">
    <!-- Aurora Paradise Ivory Tile Background -->
    <path
        android:pathData="{rect_path}"
        android:fillColor="{rect_fill}" />
    <!-- Gradient Icon Path -->
    <path
        android:pathData="{path_d}"
        android:strokeWidth="6"
        android:strokeLineCap="round"
        android:strokeLineJoin="round">
        <aapt:attr name="android:strokeColor">
            <gradient
                android:startX="0"
                android:startY="0"
                android:endX="100"
                android:endY="100"
                android:type="linear">
                <item android:offset="0.0" android:color="{start_color}" />
                <item android:offset="1.0" android:color="{end_color}" />
            </gradient>
        </aapt:attr>
    </path>
</vector>
"""
    with open(xml_path, 'w', encoding='utf-8') as out:
        out.write(xml_content)

print(f"Successfully generated all {len(files)} VectorDrawables in {res_dest}")
