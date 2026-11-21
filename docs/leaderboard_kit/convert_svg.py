import os
import glob
import xml.etree.ElementTree as ET

src_dir = 'docs/leaderboard_kit/Leaderboard_Animation_Kit/assets/svg'
res_dest = 'app/src/main/res/drawable'

os.makedirs(res_dest, exist_ok=True)

def circle_to_path(cx, cy, r):
    return f"M {cx - r} {cy} a {r} {r} 0 1 0 {2 * r} 0 a {r} {r} 0 1 0 {-2 * r} 0"

def rect_to_path(x, y, w, h, rx=0):
    if rx <= 0:
        return f"M {x} {y} h {w} v {h} h {-w} Z"
    return (
        f"M {x + rx} {y} "
        f"h {w - 2 * rx} "
        f"a {rx} {rx} 0 0 1 {rx} {rx} "
        f"v {h - 2 * rx} "
        f"a {rx} {rx} 0 0 1 {-rx} {rx} "
        f"h {-w + 2 * rx} "
        f"a {rx} {rx} 0 0 1 {-rx} {-rx} "
        f"v {-h + 2 * rx} "
        f"a {rx} {rx} 0 0 1 {rx} {-rx} Z"
    )

files = glob.glob(os.path.join(src_dir, '*.svg'))
print(f"Converting {len(files)} SVGs...")

for f in files:
    fname = os.path.basename(f)
    name, _ = os.path.splitext(fname)
    xml_path = os.path.join(res_dest, f"{name}.xml")
    tree = ET.parse(f)
    root = tree.getroot()
    vb = root.attrib.get('viewBox', '0 0 256 256').split()
    vw = float(vb[2])
    vh = float(vb[3])

    xml_lines = [
        '<vector xmlns:android="http://schemas.android.com/apk/res/android"',
        f'    android:width="{vw}dp"',
        f'    android:height="{vh}dp"',
        f'    android:viewportWidth="{vw}"',
        f'    android:viewportHeight="{vh}">'
    ]

    for el in root:
        tag = el.tag.split('}')[-1]
        fill = el.attrib.get('fill', '#000000')
        stroke = el.attrib.get('stroke')
        sw = el.attrib.get('stroke-width')
        fill_alpha = el.attrib.get('fill-opacity')
        stroke_alpha = el.attrib.get('stroke-opacity')

        path_data = None
        if tag == 'circle':
            cx = float(el.attrib['cx'])
            cy = float(el.attrib['cy'])
            r = float(el.attrib['r'])
            path_data = circle_to_path(cx, cy, r)
        elif tag == 'rect':
            x = float(el.attrib.get('x', 0))
            y = float(el.attrib.get('y', 0))
            w = float(el.attrib['width'])
            h = float(el.attrib['height'])
            rx = float(el.attrib.get('rx', 0))
            path_data = rect_to_path(x, y, w, h, rx)
        elif tag == 'path':
            path_data = el.attrib['d']

        if path_data:
            p_attrs = [f'android:pathData="{path_data}"']
            if fill and fill != 'none':
                p_attrs.append(f'android:fillColor="{fill}"')
            if fill_alpha:
                p_attrs.append(f'android:fillAlpha="{fill_alpha}"')
            if stroke and stroke != 'none':
                p_attrs.append(f'android:strokeColor="{stroke}"')
            if sw:
                p_attrs.append(f'android:strokeWidth="{sw}"')
            if stroke_alpha:
                p_attrs.append(f'android:strokeAlpha="{stroke_alpha}"')
            xml_lines.append('    <path ' + ' '.join(p_attrs) + ' />')

    xml_lines.append('</vector>\n')
    with open(xml_path, 'w', encoding='utf-8') as out:
        out.write('\n'.join(xml_lines))

print("Successfully converted all 30 SVGs to VectorDrawables in app/src/main/res/drawable/")
