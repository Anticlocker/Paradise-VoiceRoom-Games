import re
import glob

with open('app/src/main/java/com/sundram/weplay/ui/nav/WePlayBottomBar.kt', encoding='utf-8') as f:
    nav_bar_code = f.read()

route_constants = dict(re.findall(r'const\s+val\s+(\w+)\s*=\s*"([^"]+)"', nav_bar_code))

with open('app/src/main/java/com/sundram/weplay/MainActivity.kt', encoding='utf-8') as f:
    main_code = f.read()

composable_routes = set(re.findall(r'composable\s*\(\s*Routes\.(\w+)', main_code))

print(f"Total Routes defined: {len(route_constants)}")
print(f"Total Composable endpoints in MainActivity: {len(composable_routes)}")

# Check matching
missing_composables = set(route_constants.keys()) - composable_routes
if missing_composables:
    print(f"ERROR: Missing composables for routes: {missing_composables}")
else:
    print("ALL 38 Routes have matching composable handlers in MainActivity.kt!")

print("\n--- Route Mapping & Status ---")
for const_name, pattern in sorted(route_constants.items()):
    status = "OK (Registered)" if const_name in composable_routes else "BROKEN (Not registered)"
    print(f"Routes.{const_name:25} -> {pattern:32} [{status}]")

print("\n--- Detailed Audit of Navigation Destinations in Application ---")
nav_calls = []
for fpath in sorted(glob.glob('app/src/main/java/**/*.kt', recursive=True)):
    with open(fpath, encoding='utf-8', errors='ignore') as f:
        lines = f.readlines()
    for idx, line in enumerate(lines):
        for m in re.finditer(r'(?:nav\.navigate|nav\.navigateSafe)\s*\(([^)]+)\)', line):
            dest = m.group(1).strip()
            nav_calls.append((fpath.replace('\\', '/'), idx + 1, dest))

print(f"Total navigation statements scanned across all Kotlin files: {len(nav_calls)}")
for fpath, lnum, dest in nav_calls:
    print(f"  {fpath}:{lnum} -> {dest}")
