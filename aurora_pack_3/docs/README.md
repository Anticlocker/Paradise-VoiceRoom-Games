# Aurora Paradise 3.0 — Android asset starter pack

Includes 8 original procedural celestial backgrounds (720×1280 WebP), 10 cropped concept-art game/gift/currency tiles (PNG), 8 SVG decorative frames, 9 SVG interface icons, and the full generated visual asset catalog for art direction.

## Important limitations
The cropped game/gift tiles are **concept samples**, not transparent, production-grade isolated 3D objects. Some have existing colored backdrops or borders. The catalog is a reference sheet, **not** a spritesheet or directly reusable whole-screen UI. The backgrounds are original atmospheric gradients, not exact reproductions of the illustrated palace scene. Do not treat these assets as a finished full art library.

## Jetpack Compose integration
1. Convert SVG to VectorDrawable using Android Studio Vector Asset where supported, or render with an appropriate SVG loader.
2. Copy WebP and PNG to app/src/main/res/drawable-nodpi/ (or appropriate density resources).
3. Use backgrounds with ContentScale.Crop behind foreground composables and a contrast scrim.
4. Use the generated concept catalog to commission/generate individual isolated 3D assets and illustrated event banners.
5. Keep wallet values, offers and events dynamic; never embed prices or countdowns in graphics.
6. Optimize artwork, check licenses and test on physical devices.

## Asset categories still requiring bespoke production
Isolated transparent hero treasure chest, unique character/game illustrations, fantasy palace header, event banners, animated avatar frames, PK battle effects and Lottie motion files.
