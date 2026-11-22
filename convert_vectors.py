import os

frames = [
    ('aurora3_frame_gold', '#FFD36B', '#FFF3C4', 50, 44),
    ('aurora3_frame_cyan', '#54CFFF', '#E0F7FF', 50, 44),
    ('aurora3_frame_pink', '#FF78C4', '#FFE4F3', 50, 44),
    ('aurora3_frame_violet', '#B89BFF', '#EDE4FF', 50, 44),
    ('aurora3_seat_gold', '#FFD36B', '#FFF3C4', 44, 38),
    ('aurora3_seat_cyan', '#54CFFF', '#E0F7FF', 44, 38),
    ('aurora3_seat_pink', '#FF78C4', '#FFE4F3', 44, 38),
    ('aurora3_seat_violet', '#B89BFF', '#EDE4FF', 44, 38),
]

os.makedirs('app/src/main/res/drawable', exist_ok=True)

for name, color, light_color, r1, r2 in frames:
    xml = f'''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="128dp"
    android:height="128dp"
    android:viewportWidth="128"
    android:viewportHeight="128">
    <!-- Outer faint glow ring -->
    <path
        android:strokeColor="{color}"
        android:strokeAlpha="0.24"
        android:strokeWidth="10"
        android:pathData="M64,64 m-{r1},0 a{r1},{r1} 0 1,0 {r1*2},0 a{r1},{r1} 0 1,0 -{r1*2},0" />
    <!-- Inner metallic ring -->
    <path
        android:strokeColor="{color}"
        android:strokeWidth="7"
        android:pathData="M64,64 m-{r2},0 a{r2},{r2} 0 1,0 {r2*2},0 a{r2},{r2} 0 1,0 -{r2*2},0" />
    <!-- Crest star at top -->
    <path
        android:fillColor="{color}"
        android:pathData="M64,4 L70,20 L86,26 L70,32 L64,48 L58,32 L42,26 L58,20 Z" />
    <!-- Sparkle accents -->
    <path
        android:fillColor="{light_color}"
        android:pathData="M26,94 m-4,0 a4,4 0 1,0 8,0 a4,4 0 1,0 -8,0" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M104,90 m-3,0 a3,3 0 1,0 6,0 a3,3 0 1,0 -6,0" />
</vector>'''
    with open(f'app/src/main/res/drawable/{name}.xml', 'w', encoding='utf-8') as f:
        f.write(xml)
    print(f'Wrote app/src/main/res/drawable/{name}.xml')

# Nav icons
nav_icons = {
    'aurora3_ic_nav_games': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M20,22 h24 a10,10 0 0,1 10,10 v4 a10,10 0 0,1 -10,10 h-24 a10,10 0 0,1 -10,-10 v-4 a10,10 0 0,1 10,-10 z M22,29 v12 M16,35 h12 M39,33 h1 M45,39 h1" />
</vector>''',
    'aurora3_ic_nav_rooms': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M12,35 v-5 a20,20 0 0,1 40,0 v5 M14,32 h1 a4,4 0 0,1 4,4 v10 a4,4 0 0,1 -4,4 h-1 a4,4 0 0,1 -4,-4 v-10 a4,4 0 0,1 4,-4 z M49,32 h1 a4,4 0 0,1 4,4 v10 a4,4 0 0,1 -4,4 h-1 a4,4 0 0,1 -4,-4 v-10 a4,4 0 0,1 4,-4 z" />
</vector>''',
    'aurora3_ic_nav_chats': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M10,16 h44 v29 H29 L16,54 v-9 h-6 z M20,26 h24 M20,34 h17" />
</vector>''',
    'aurora3_ic_nav_discover': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M32,10 a22,22 0 1,0 0,44 a22,22 0 1,0 0,-44 z M40,24 L35,36 L24,40 L29,28 Z" />
</vector>''',
    'aurora3_ic_nav_me': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M32,12 a10,10 0 1,0 0,20 a10,10 0 1,0 0,-20 z M13,53 C13,35 51,35 51,53" />
</vector>''',
    'aurora3_ic_nav_gift': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M16,27 h32 a4,4 0 0,1 4,4 v21 a4,4 0 0,1 -4,4 h-32 a4,4 0 0,1 -4,-4 v-21 a4,4 0 0,1 4,-4 z M9,27 h46 v-9 H9 z M32,18 v38 M32,18 c-23,0 -16,-20 -3,-8 z M32,18 c23,0 16,-20 3,-8 z" />
</vector>''',
    'aurora3_ic_nav_plus': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M32,12 v40 M12,32 h40" />
</vector>''',
    'aurora3_ic_nav_back': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M40,10 L18,32 L40,54" />
</vector>''',
    'aurora3_ic_nav_star': '''<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="64dp"
    android:height="64dp"
    android:viewportWidth="64"
    android:viewportHeight="64">
    <path
        android:strokeColor="#F8F1FF"
        android:strokeWidth="4"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:pathData="M32,6 L39,24 L58,25 L43,38 L48,57 L32,46 L16,57 L21,38 L6,25 L25,24 Z" />
</vector>''',
}

for name, xml in nav_icons.items():
    with open(f'app/src/main/res/drawable/{name}.xml', 'w', encoding='utf-8') as f:
        f.write(xml)
    print(f'Wrote app/src/main/res/drawable/{name}.xml')
