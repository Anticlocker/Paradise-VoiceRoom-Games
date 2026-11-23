# Repository Guide

## Scope and architecture

- Runtime code is in `app/src/` and `server/`. The large `docs/`, `aurora_pack_3/`, `repo_art_kit/`, and `design-assets/` trees are reference/implementation packs and are not Gradle modules.
- Treat the root README claim that there is no backend or network access as stale. `settings.gradle.kts` has one Android module (`:app`), while `server/` is a separate Node/PostgreSQL system.
- `MainActivity.kt` owns the Compose `NavHost`; route constants and builders live unexpectedly in `ui/nav/WePlayBottomBar.kt`.
- Most Android features still use demo data and `SharedPreferences` stores. `api.base.url` only populates the currently unused `AppConfig`; the app is not wired to `server/server.mjs`.
- Voice is the exception: both `livekit.url` and `livekit.token.url` in git-ignored `local.properties` select `LiveKitVoiceRoomEngine`; otherwise it uses `DemoVoiceRoomEngine`. Never put the LiveKit API secret in app config; `livekit.token.key` is compiled into the APK.
- `server/server.mjs` mounts identity, economy, social, messaging, notifications, rooms, and support over one shared PostgreSQL database. `server/token-server.mjs` is a separate dependency-free LiveKit token minter. Migrations `003`/`004` add family/ranking schemas, but no corresponding HTTP modules are mounted yet.

## Android commands

- Use JDK 17. In this Windows workspace the wrapper needs `$env:JAVA_HOME = 'C:\Users\Admin\.jdks\temurin-17'` before it can read the matching user-specific path in `gradle.properties`.
- Build: `.\gradlew.bat :app:assembleDebug` (APK: `app/build/outputs/apk/debug/app-debug.apk`).
- Unit suite: `.\gradlew.bat :app:testDebugUnitTest`.
- Focus one class: `.\gradlew.bat :app:testDebugUnitTest --tests 'com.sundram.weplay.NavigationRoutesUnitTest'`; append `.testBottomBarTabs` to focus one method.
- Android lint: `.\gradlew.bat :app:lintDebug`.
- Unit tests get JUnit only from `app/libs/junit-4.13.2.jar` and `hamcrest-core-1.3.jar`. The repository-wide `*.jar` ignore means those files are absent from a clean clone unless restored locally.
- Several unit tests inspect source text or count resource files (not just behavior), notably `MeRoyalPageUnitTest`, `PhoenixAvatarFrameUnitTest`, `ChatsHybridUnitTest`, and `LeaderboardAndFramesUnitTest`. Renames, formatting, and asset additions can require intentional assertion updates.

## Server commands

- Requires Node >= 22. Install the lockfile from the repository root with `npm --prefix server ci`.
- Server tests require PostgreSQL and default to `postgresql://weplay:weplay@127.0.0.1:54329/postgres`; they create isolated `weplay_test_<module>` databases, so the role must be allowed to create databases.
- Start the matching local database with `docker run -d --name weplay-pg-test -e POSTGRES_USER=weplay -e POSTGRES_PASSWORD=weplay -p 127.0.0.1:54329:5432 postgres:17-alpine`, then run all tests with `npm --prefix server test`.
- Focus a server file from the root with `node --test server/identity/identity.test.mjs`; add `--test-name-pattern='signup creates'` before the file path to focus a test name.
- Start the platform API with `DATABASE_URL` set: `node server/server.mjs`. Boot automatically applies shared migrations and refreshes the economy catalogue; there is no separate migration command.

## Database and verification traps

- Never edit, rename, or insert before an applied file in `server/db/migrations/`. The runner uses each sorted file's array position as its version rather than parsing the numeric filename; append the next numbered migration only.
- Keep live presence, voice seats/mic state, and media bytes out of PostgreSQL. LiveKit owns ephemeral room state; the database stores persistent room metadata and media URLs only.
- Economy owns all wallet, ledger, gift, and top-up writes. Messaging may reference a completed gift transaction but must not move coins.
- `.github/workflows/pr-agent.yml` performs AI review only; no CI workflow builds, lints, or tests the code.
