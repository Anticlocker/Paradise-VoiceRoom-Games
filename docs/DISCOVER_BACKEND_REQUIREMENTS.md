# Discover 3.0 — Backend Requirements

**Version:** Discover 3.0  
**Date:** 2026-11-22  
**App:** VYBORA (`com.sundram.weplay`)

This document describes the backend features required to fully support the Discover 3.0 implementation, documents which features are currently stubbed or device-local, and specifies what must be implemented server-side before each feature can be promoted to production.

---

## Current Architecture Summary

| Component | Status |
|---|---|
| Supabase (database) | Configured for auth and room data |
| Supabase Storage | **Not configured** for Discover media uploads |
| Realtime feed subscription | **Not implemented** for Discover moments feed |
| Post persistence | Local `SharedPreferences` only |
| Image upload | **No backend** — URIs are device-local |
| Like/comment counts | Local `SharedPreferences` only |

---

## 1. Post Image Upload

### Current behaviour
Images selected via the Android Photo Picker are held as `android.net.Uri` references in device RAM and in-memory state only. They are **not uploaded** to any server. When the user taps "Publish Moment", the post is recorded locally with no image URLs.

The UI does **not** claim upload success. No fake progress bar or false confirmation is shown.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| Configure Supabase Storage bucket `discover-moments` with appropriate RLS policies | High | Public read, authenticated write |
| Upload each selected URI to `discover-moments/{userId}/{postId}/{filename}` | High | Use Supabase Kotlin client `storage().from("discover-moments").upload(...)` |
| Return public CDN URLs for each uploaded image | High | Store in post record |
| Handle upload failure with user-visible error and retry | Medium | Show toast / inline error |
| Enforce file type (JPEG/PNG/WebP) and max size (10 MB per image) server-side | Medium | Supabase Storage policy |
| Virus/content scanning for uploaded media | Low | Optional CSAM / moderation hook |

### Integration point in app
`CreateMomentBottomSheet` calls `onPost(textInput.trim(), selectedCategory)`. The callback should be extended to `onPost(text: String, tag: String, imageUris: List<Uri>)` and the caller (`DiscoverScreen`) should pass image URIs to a repository layer that handles the upload chain before writing the post record.

---

## 2. Post Feed (Moments)

### Current behaviour
Posts are stored in `SharedPreferences` as a JSON list keyed by `"discover_posts"`. All reads and writes are device-local. There is no server-authoritative feed, no multi-device sync, and no other users can see any post.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| `discover_posts` table in Supabase (columns: `id`, `user_id`, `text`, `tag`, `image_urls[]`, `created_at`, `likes_count`, `comments_count`) | High | |
| Row-level security: anyone can read; only author can insert/delete | High | |
| REST endpoint or Supabase auto-REST for feed pagination (`order=created_at desc`, `limit=20`, `offset=N`) | High | |
| Supabase Realtime subscription on `discover_posts` for live feed updates | Medium | Channel: `public:discover_posts` |
| Replace `SharedPreferences` store in `DiscoverModels.kt` with Supabase repository | High | Keep `InMemorySharedPreferences` for unit tests only |

---

## 3. Like / Unlike

### Current behaviour
Likes are toggled in `SharedPreferences` per post ID. No server record is written.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| `discover_post_likes` table (`post_id`, `user_id`, `created_at`, unique on pair) | High | |
| Upsert / delete via Supabase client on like toggle | High | |
| Increment / decrement `likes_count` on `discover_posts` via Supabase trigger or RPC | Medium | Prevents race conditions |
| Return updated like count to client | Medium | |

---

## 4. Comments

### Current behaviour
Comment count is displayed as a static number. No comment list or comment submission is implemented.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| `discover_post_comments` table (`id`, `post_id`, `user_id`, `text`, `created_at`) | Medium | |
| Comment list screen / bottom sheet | Medium | UI not yet built |
| Comment submission API | Medium | |
| `comments_count` incremented via trigger on `discover_posts` | Medium | |

---

## 5. Share

### Current behaviour
Tapping Share triggers `android.content.Intent.ACTION_SEND` with the post text via the system share sheet. No deep link or in-app share is implemented.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| Dynamic link generation (Firebase Dynamic Links or Supabase Edge Function) returning `https://vybora.app/discover/{postId}` | Low | Enables in-app deep linking |
| Deep link handler in `MainActivity` to open the post detail on receipt | Low | |

---

## 6. Nearby Players

### Current behaviour
Nearby player list is populated from a mock data source. No real geolocation or proximity query is performed.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| `user_locations` table or Supabase Edge Function for proximity query (PostGIS `ST_DWithin`) | Medium | |
| Write user's last-known location on app foreground (with explicit opt-in permission) | Medium | |
| Respect `isVisibleToOthers` toggle — do not write location when off | High | Privacy requirement |
| Automatic location TTL (expire records older than 10 minutes) | Medium | |

---

## 7. Live Events / Events Calendar

### Current behaviour
Live Events card navigates to a stub screen. No real events are fetched.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| `discover_events` table (`id`, `title`, `description`, `starts_at`, `ends_at`, `image_url`, `type`) | Medium | |
| Feed endpoint, ordered by `starts_at` | Medium | |
| Event registration / RSVP (`discover_event_registrations`) | Low | |

---

## 8. Trending Tags

### Current behaviour
Trending tags are hardcoded in the UI.

### Required backend work

| Task | Priority | Notes |
|---|---|---|
| Supabase View or Materialized View aggregating tag usage counts from `discover_posts` | Low | Refresh on a schedule |
| Edge Function to return top-N tags | Low | |

---

## Summary Priority Table

| Feature | Backend Priority | Estimated Effort |
|---|---|---|
| Post image upload (Supabase Storage) | 🔴 High | 1–2 days |
| Server-authoritative post feed | 🔴 High | 2–3 days |
| Like / unlike persistence | 🔴 High | 1 day |
| Realtime feed subscription | 🟡 Medium | 1–2 days |
| Comments | 🟡 Medium | 2–3 days |
| Nearby players geolocation | 🟡 Medium | 2–3 days |
| Live Events data | 🟡 Medium | 1–2 days |
| Trending tags aggregation | 🟢 Low | 1 day |
| Share deep links | 🟢 Low | 1 day |

> **Important:** Until the backend is implemented, the app behaves correctly and honestly — it does not simulate upload success, does not fabricate server responses, and does not claim data is persisted beyond the local device.
