# Paradise — Third-Party Licenses & Attribution

Audit of libraries and non-proprietary components used in the Paradise Android client (`app/build.gradle.kts` & `app/libs/`).

## 1. Runtime Dependencies
| Component | Coordinate / Origin | License | Notes |
| :--- | :--- | :--- | :--- |
| Jetpack Compose BOM | `androidx.compose:compose-bom:2024.12.01` | Apache License 2.0 | UI toolkit, foundation, Material 3, tooling |
| AndroidX Core KTX | `androidx.core:core-ktx:1.15.0` | Apache License 2.0 | Kotlin extensions & edge-to-edge helpers |
| Activity Compose | `androidx.activity:activity-compose:1.9.3` | Apache License 2.0 | ComponentActivity Compose hosting & permission launchers |
| Navigation Compose | `androidx.navigation:navigation-compose:2.8.5` | Apache License 2.0 | `NavHost` routing & tab state preservation |
| Lifecycle KTX / ViewModel | `androidx.lifecycle:lifecycle-*-ktx:2.8.7` | Apache License 2.0 | Voice room lifecycle & state observation |
| LiveKit Android SDK | `io.livekit:livekit-android:2.30.0` | Apache License 2.0 | WebRTC voice room client (conditional when configured) |
| Coil Compose | `io.coil-kt:coil-compose:2.7.0` | Apache License 2.0 | Async avatar / moment image decoding |
| Lottie Compose | `com.airbnb.android:lottie-compose:6.6.0` | Apache License 2.0 | Vector celebration & gift animations |

## 2. Test Dependencies
| Component | Location | License |
| :--- | :--- | :--- |
| JUnit 4 | `app/libs/junit-4.13.2.jar` | Eclipse Public License 1.0 (EPL-1.0) |
| Hamcrest Core | `app/libs/hamcrest-core-1.3.jar` | BSD 3-Clause License |

## 3. Artwork & IP Actions Taken
- Unused `aurora3_tile_uno.png` (UNO® trademark risk) was **removed** from `app/src/main/res/drawable-nodpi/`.
- Playable board/social games are named cleanly (`Ludo Classic`, `Who's the Spy`).
- Preview-only game cards (`Petrol Pump Simulator`, `Paint to Hide`, `Trickster's Cards`, `Hide And Seek`, `Sci-Fi Impostor`, `Card Matching`, `Skyline Drift`) must retain original artwork before Play Store production submission (see `ASSET_PROVENANCE.csv`).
- Notice surfaced in-app via `Settings > Support & Legal > Open Source Licenses` and `About > Open source licenses`.
