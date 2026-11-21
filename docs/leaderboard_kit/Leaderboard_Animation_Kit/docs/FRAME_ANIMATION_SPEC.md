# Frame animation specification (original design)

Use native Compose Canvas for scalable animation around avatar images. The SVGs are static shape templates, not animated files.

Frame states: LOCKED, AVAILABLE, OWNED, EQUIPPED, EXPIRED.
Tiers: common, rare, epic, legendary, mythic, celestial. Avoid promising exact WePlay levels.

| Tier | Duration | Motion | Layers |
|---|---|---|---|
| common | static | none | base ring |
| rare | 2200 ms | alpha pulse 0.65–1 | cyan glow + ring |
| epic | 4200 ms | clockwise particle rotation | violet ring + 8 particles |
| legendary | 5600 ms | counter-rotating orbit + twinkle | gold ring + 2 orbital bands + 12 stars |
| mythic | 6400 ms | slow crown float, orbiting gems | crown + two gold rings + 3 jewels |
| celestial | 8000 ms | aurora sweep and jewel orbit | multicolor ring + rays + gems |

Use `rememberInfiniteTransition` only while the component is visible and animation enabled; prefer lifecycle-aware state and disable under reduced-motion. Provide deterministic static fallback. At 48–72dp avatars reduce complexity to one glow and one ring; at 96–128dp use full effects. Never animate entire LazyColumn rows continuously. Clip photos inside inner ring and draw decorative layers outside. Respect parent bounds so glow isn't cut off.

Backend grants entitlements; client only previews. Grant types: level, achievement, VIP, shop, event, family. Grant and revoke via verified server state; purchases must use eligible Play Billing and verified backend fulfillment. Equipped frame must be stored server-side and broadcast to relevant clients.
