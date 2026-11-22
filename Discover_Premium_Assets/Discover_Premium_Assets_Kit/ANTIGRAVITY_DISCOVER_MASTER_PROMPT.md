# DISCOVER 3.0 — IMPLEMENTATION MASTER PROMPT

You are a senior Kotlin/Jetpack Compose Android developer, visual designer, backend engineer, accessibility specialist and QA lead. Modify my EXISTING Android application, do not create a new project. My application name is NOT finalized. The first attached screenshot is OUR CURRENT Discover screen; the second screenshot is a WePlay visual reference for colorful glossy icons and clean whitespace. Never copy WePlay icons or layouts exactly. Use supplied original assets and retain the current project's white/lavender/cyan/peach visual language established in Games and Voice Room.

## Nonnegotiable changes
1. REMOVE the `VYBORA 2.0` chip/text in Discover heading completely. Search project for other hardcoded VYBORA branding within Discover and remove it, but do not rename Android package, applicationId or unrelated files. Keep header `Discover` with subtitle `Find your people, stories and spaces` unless existing copy localization requires equivalent. Ensure alignment/spacing after removal.
2. REMOVE the floating `Special Offer / 48h Flash Pack` button from Discover and its Discover-only overlay/click handlers. Do not delete the global offer/gift-pack feature or its entry points on other screens. Make sure the overlay no longer blocks feed scrolling or bottom navigation.
3. Replace repeated generic icons with original icons from supplied pack: `discover_families`, `discover_nearby`, `discover_clubs_spaces`, `discover_live_events`, `discover_moments`, plus search, post, gallery, camera, trending, like, comment, share, location, filter, notification, profile, empty_feed, privacy, upload, edit, close, hashtag, events_calendar. Import PNGs into `app/src/main/res/drawable-nodpi/` using valid lowercase names. SVGs are editable sources, WebPs are alternatives. Do not accidentally import three variants under the same drawable resource name. Ensure icons are rendered with their original colors, no tint; use ContentScale.Fit and consistent 56–76dp display sizes, with sufficient internal padding. Images are static, not Lottie/3D animations.
4. Preserve the EXISTING Discover screen's useful architecture: top app bar with Post button, search field, Moments feature card, 2x2 feature grid (Families, Nearby, Clubs & Spaces, Live Events), Trending Topics horizontal chips, Community Moments feed and five-tab bottom navigation. Improve visual hierarchy, spacing and card corners (20–24dp) but do not blindly replace with the sparse second screenshot.
5. **Post creation must support images**. Implement functional Android Photo Picker via ActivityResultContracts.PickMultipleVisualMedia (or supported modern equivalent) with images-only MIME restrictions, up to 5 images, no broad storage permission. Also allow text-only posts, image-only posts, and text+image posts. Optional camera capture only if existing permissions/storage flow is properly configured; do not force it. Provide image preview grid, remove/reorder if feasible, per-image loading, max file size, supported types, content validation and clear error messages. Never publish until uploads complete and backend confirms post creation.

## Posting architecture and security
- Inspect current authentication, repository, backend (Node/Supabase if present), storage bucket, posts table, RLS policies, existing Moments endpoints, and image loader. Reuse existing services. Never hardcode service role keys in Android app.
- On opening Post composer, show avatar, text input, gallery button, selected image previews, audience selector only if privacy backend supports it, disabled Post while empty/uploading, character counter and keyboard/insets support.
- Use Android content URIs with ContentResolver, stream safely; do not assume filesystem path. Validate actual MIME and file size; downsample/compress large images with EXIF rotation handling; avoid OOM. Upload through authenticated backend or Supabase Storage with restrictive policies. Use generated object keys, not user-supplied paths; secure access rules and content moderation as appropriate. Persist only metadata and authorized media URLs/keys.
- Recommended model if schema lacks it: posts(id, author_id, body, visibility, created_at, updated_at, status), post_media(id, post_id, storage_key, mime_type, width, height, position), post_likes, post_comments. Use proper foreign keys/indexes and RLS. Adapt to current schema; do not destructively replace existing posts.
- Handle partial upload failure: cancel/retry, cleanup orphaned uploads, preserve draft when navigation is interrupted if feasible, idempotent publish request. Prevent duplicate posts on double tap. On success return to Discover, refresh Moments feed, show the new post with real image thumbnails, text, author, timestamps, like/comment/share actions backed by existing functionality. Images open full-screen viewer with swipe and accessible close. If upload backend isn't configured, show honest setup/blocker and keep composer functional locally without falsely claiming publication.
- Respect user blocks, reporting, moderation, private accounts, age restrictions if applicable. Use server-authoritative like counts and comment counts; no fabricated feed items in production.

## Feature cards and clicks
- Families -> existing Family Hub discovery/create/join route, preserve Family system from previous implementation.
- Nearby -> explicit permission explanation; request foreground location only on user action, use approximate location if sufficient, handle denied/disabled GPS and settings redirect. Never silently track precise location or publish coordinates. If backend unsupported show honest unavailable state, not fake distances.
- Clubs & Spaces -> real group/community discovery list, search/filter, details, join/leave where backend supports. If currently unimplemented, add repository and meaningful empty state; do not create fake active counts.
- Live Events -> real events list with upcoming/live/completed, time zone display, event details, join/register when supported, empty state if none.
- Moments feature card -> open feed; `+ Post Moment` -> composer; `Explore Feed` -> scroll or navigate to feed. Search -> real query results or scoped suggestions with loading/empty states. Trending chips -> filter posts by tag; if tags absent, show clear empty state.
- All taps must navigate with existing NavHost and work with Android back. Bottom tabs must remain clickable after opening nested screens; avoid overlays intercepting touches.

## Design spec
- Background #FCFAFF / white; cards #FFFFFF with subtle lavender or peach wash, thin neutral border and low-elevation shadow. Text #29243B, secondary #827C91, cyan #28BED4, lavender #8D6CE5, peach #FFB17A.
- Header `Discover` 24–28sp bold, no brand badge. Keep Post action aligned right.
- Search 48–52dp tall with soft border; card spacing 12–16dp, outer margins 16–20dp. Moments card ~120–150dp tall; 2-column grid equal heights and tasteful icon prominence. Use illustrated icons rather than repeating the same speech bubble everywhere.
- Empty feed: original `discover_empty_feed` illustration, helpful `No moments yet` copy and `Create a post` action. No huge unexplained blank screen. Do not add fabricated posts or mock popularity numbers in release builds.
- Make UI adapt to small phones, large font sizes, dark system bars and edge-to-edge Android. Respect reduced motion and accessibility touch target >=48dp.

## Implementation steps and verification
1. Audit existing Discover composables, NavHost, repository, auth, Moments data, storage, feature card destinations. Document files and current behavior.
2. Import original icons; implement new header, cards and remove brand chip/offer overlay. Verify screenshots and compile.
3. Build functional post composer with image picker, previews and validation. Integrate uploads, create post transaction, feed display and image viewer. Verify real authenticated account flow.
4. Wire Families, Nearby, Clubs, Events, search and tags; reuse actual services and clear empty/unavailable states.
5. Fix regressions and test Android back, keyboard, scroll, nav bar, network loss, permissions denied, images >limit, image-only, text-only, multi-image, upload failure, rapid taps, rotation/process death.
6. Run actual `./gradlew assembleDebug` and available unit/UI tests and lint. Report true results, exact changed files and any backend setup required. Do not push GitHub automatically.

## Deliverables
- Implemented Discover UI and image-enabled Moments composer.
- `docs/DISCOVER_CHANGELOG.md` listing every changed file and screen.
- `docs/DISCOVER_FEATURE_TEST_MATRIX.md` with pass/fail/not-tested for each click and posting scenario.
- `docs/DISCOVER_BACKEND_REQUIREMENTS.md` for any unavailable storage or APIs.
- Screenshot evidence of Discover, composer with 3 images, and published post if backend configured.

START IMPLEMENTATION NOW. DO NOT STOP AT A PLAN OR STATIC MOCKUP.
