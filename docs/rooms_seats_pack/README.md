# VYBORA — Game Rooms, Create Room & Partner / Attach Seats Asset Pack

**Format:** Android-ready WebP, `drawable-nodpi` resources plus reference documentation. 35 **new incremental assets**, not a replacement for earlier VYBORA asset packs.

## Installation

1. Open existing Kotlin + Jetpack Compose project in Antigravity.
2. Extract this folder somewhere Antigravity can read.
3. Paste `ANTIGRAVITY_MASTER_PROMPT.md` into the agent.
4. Agent should inspect project and then copy the needed `vb_rs_*.webp` files from `android/app/src/main/res/drawable-nodpi` into **the existing Android app module** at `src/main/res/drawable-nodpi`.
5. Do **not** add the supplied `android/app` as a new module. Do not indiscriminately overwrite prior `vb_vr_*` resources.
6. Check `docs/asset_manifest.json` for exact names, sizes and uses. Gradle-build then compare on Android phone.

## Contents and usage

- 12 original neon 3D illustration cutouts, including partner bonding, create-room, privacy, microphone, games, PK and dice.
- 6 backgrounds for Game Rooms, Create Room, advanced settings, partner/attach cards and empty rooms.
- 4 transparent avatar-frame overlays for partner/attached/selected seats.
- 3 standalone seat-state illustrations.
- 3 connector variants for bond states. Draw real connections with Compose `Canvas` from actual seat coordinates; images are optional decorations.
- 3 light gradient card skins for native rounded layout.
- 4 themed DEMO cover illustrations (not real participant/room covers).
- `kotlin/SeatAndBondModels.kt.txt` — data/state reference to adapt into existing models.
- Full Antigravity implementation prompt.

## Design rules

Existing branding: light pearl white + lavender + cyan on discovery/create pages; dark Aurora in the Joined Room. Host plus eight regular microphone seats always. Optional partner on host's RIGHT. Attach Seats connects two consenting occupied regular seats; it is *not* the same as Partner Seat.

All WebPs are static graphics; mic/seat animation, interaction, real backend and LiveKit voice require actual code. Icon assets are generated original illustrations. Some bright-gloss edges may be softly feathered; inspect on real devices. Reuse earlier VYBORA art where better, rather than claiming exact replication of any third-party artwork.

Use `R.drawable.vb_rs_bond_heart`, `R.drawable.vb_rs_frame_partner`, etc. only after importing the actual assets.
