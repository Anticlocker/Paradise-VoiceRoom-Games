# Vybora — Functionality Matrix
**Phase 0 · Generated: 2026-11-19**
**Auditor: Antigravity (Senior Android / Compose Architect)**

---

> [!NOTE]
> Status values: `COMPLETE_LOCAL` · `COMPLETE_LIVE` · `DEMO_ONLY` · `BLOCKED_INTEGRATION` · `BROKEN` · `NOT_STARTED`

---

## AuthScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Enter display name | Name accepted, stored | `COMPLETE_LOCAL` | Manual ✅ | — |
| Select gender | Gender stored in UserProfile | `COMPLETE_LOCAL` | Manual ✅ | — |
| Select DOB via date picker | DOB stored | `COMPLETE_LOCAL` | Manual ✅ | — |
| Pick profile photo | Photo path saved to UserProfile | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Save / Create Profile | Navigates to main app | `COMPLETE_LOCAL` | Manual ✅ | — |
| OTP verification | Verify phone number | `NOT_STARTED` | ❌ | OTP backend |
| Avatar selection gallery | Choose built-in avatar | `NOT_STARTED` | ❌ | Avatar assets |
| Interest chip selection | Personalise feed | `NOT_STARTED` | ❌ | Interest tag model |

---

## GameLobbyScreen (HUB tab)

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Tap Ludo banner / card | Navigate to `LudoScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Spy banner / card | Navigate to `SpyScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap other game cards | Navigate to respective game | `BROKEN` | ❌ | Game screens not built |
| Tap Explore post card | Navigate to user profile / room | `BROKEN` | ❌ | Wiring not done |
| Pull-to-refresh explore feed | Reload fresh posts | `NOT_STARTED` | ❌ | ViewModel + API |
| Dark mode display | Themed background visible | `COMPLETE_LOCAL` | Manual ✅ | — (Phase 1 fix) |

---

## VoiceRoomListScreen (ROOM tab)

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View room list | Cards render with title/host/count | `DEMO_ONLY` | Manual ✅ | Real API data |
| Tap category tab | Filter rooms by category | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap room card | Navigate to `LiveVoiceRoomScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Create Room button | Navigate to `CreateVoiceRoomScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Room cover image display | Show room thumbnail via Coil | `NOT_STARTED` | ❌ | Room cover assets/API |
| Pull-to-refresh | Reload room list | `NOT_STARTED` | ❌ | API |

---

## LiveVoiceRoomScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Join room | Connect to LiveKit SFU | `COMPLETE_LIVE` | Manual ✅ | — |
| Toggle mic on/off | Mic mute/unmute, animated glow | `COMPLETE_LIVE` | Manual ✅ | — |
| View speaker seats | Seated members visible | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap seat (empty) | Seat request sheet opens | `BROKEN` | ❌ | SeatActionSheet not built |
| Tap seat (occupied) | Participant profile popup | `BROKEN` | ❌ | Participant popup not built |
| Tap gift button | Gift tray bottom sheet opens | `BROKEN` | ❌ | GiftTray composable not built |
| Send gift | Animated overlay fires, coins deducted | `NOT_STARTED` | ❌ | GiftAnimationOverlay + API |
| Tap back / leave | Exit confirmation dialog shows | `BROKEN` | ❌ | ExitConfirmDialog not built |
| Confirm leave | Disconnect LiveKit, navigate back | `COMPLETE_LIVE` | Manual ✅ | — (no confirm dialog) |
| Room settings gear | Room settings sheet opens | `BROKEN` | ❌ | RoomSettingsSheet not built |
| View room chat overlay | Messages shown in overlay | `COMPLETE_LOCAL` | Manual ✅ | — |
| Send room chat message | Message broadcast to room | `BLOCKED_INTEGRATION` | ❌ | Backend chat-in-room API |
| Emoji reaction | Floating emoji animation | `NOT_STARTED` | ❌ | Emoji reaction overlay |
| Seat invitation (host) | Invite user to seat | `NOT_STARTED` | ❌ | Seat invitation UI + API |

---

## DiscoverScreen (DISCOVER tab)

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View trending cards | Trending rooms/events display | `DEMO_ONLY` | Manual ✅ | Real API |
| View event banners | Active events listed | `DEMO_ONLY` | Manual ✅ | EventModel + API |
| Tap event card | Navigate to event detail | `BROKEN` | ❌ | Event detail screen not built |
| View explore posts | User activity posts render | `DEMO_ONLY` | Manual ✅ | Real API |
| Tap Nearby toggle | Show nearby users/rooms | `BROKEN` | ❌ | Location permission flow not built |
| Tap explore post | Navigate to user/room | `BROKEN` | ❌ | Navigation not wired |
| Pull-to-refresh | Reload all sections | `NOT_STARTED` | ❌ | API |

---

## MessagesScreen (MSG tab)

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View conversation list | Conversations rendered with avatar/unread | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap conversation row | Navigate to `ChatScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Unread count badge | Shows correct unread count | `COMPLETE_LOCAL` | Manual ✅ | — |
| New Chat FAB | Open new conversation flow | `DEMO_ONLY` | Manual ✅ | User search / friend list API |
| Search conversations | Filter inbox by name | `NOT_STARTED` | ❌ | Search bar + local filter |
| Online indicators | Show online/offline status | `NOT_STARTED` | ❌ | Presence API |

---

## ChatScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View message thread | Message bubbles render | `COMPLETE_LOCAL` | Manual ✅ | — |
| Type and send text | Message saved, displayed | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap mic button | Start audio recording | `BROKEN` | ❌ | MediaRecorder deprecated; no upload pipeline |
| Send coin gift | Deducts coins, sends gift indicator | `DEMO_ONLY` | Manual ✅ | Real gift API |
| Long-press message | Copy/delete context menu | `NOT_STARTED` | ❌ | Context menu not built |
| View typing indicator | "… typing" animation | `NOT_STARTED` | ❌ | Presence/typing API |

---

## MeScreen (ME tab)

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View profile card | Avatar/name/coins/level display | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Edit Profile | Navigate to `ProfileEditScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Settings | Navigate to `SettingsHomeScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Ranking | Navigate to `RankingScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Top Up | Navigate to `TopUpScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Gifts | Navigate to `GiftsScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Backpack | Navigate to `BackpackScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Family | Navigate to `FamilyScreen` | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Logout | Clears session, back to AuthScreen | `COMPLETE_LOCAL` | Manual ✅ | — |
| XP / Level bar | Animated progress bar shown | `NOT_STARTED` | ❌ | LevelProgressBar component |
| VIP badge | VIP frame on avatar | `NOT_STARTED` | ❌ | VipBadge component |
| Achievement showcase | Badge row displayed | `NOT_STARTED` | ❌ | AchievementBadge model |
| Notification bell | Navigate to notification center | `BROKEN` | ❌ | Notification center screen not built |
| Followers / following count | Social graph counts shown | `NOT_STARTED` | ❌ | Social API |

---

## UserProfileScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View profile | Avatar/name/bio render | `DEMO_ONLY` | Manual ✅ | Real user API |
| Tap Follow | Follow state toggled | `DEMO_ONLY` | Manual ✅ | Follow API |
| Tap Chat | Navigate to ChatScreen | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Gift | Open gift tray for this user | `BROKEN` | ❌ | Gift tray not built |
| Tap Block/Report | Submit block/report | `DEMO_ONLY` | Manual ✅ | Backend moderation API |

---

## RankingScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View leaderboard | Ranked rows with avatar/name/score | `DEMO_ONLY` | Manual ✅ | Real leaderboard API |
| Switch Wealth tab | Wealth leaderboard | `DEMO_ONLY` | Manual ✅ | Only type available |
| Switch Charm tab | Charm leaderboard | `NOT_STARTED` | ❌ | Charm data + tab UI |
| Switch Family tab | Family leaderboard | `NOT_STARTED` | ❌ | FamilyModel + tab UI |
| Switch Event tab | Event leaderboard | `NOT_STARTED` | ❌ | EventModel + tab UI |
| Tap leaderboard row | Navigate to user profile | `NOT_STARTED` | ❌ | Wiring not done |
| Animated rank reveal | Rank-up animation | `NOT_STARTED` | ❌ | Animation design |

---

## CreateVoiceRoomScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Enter room name | Name field accepts input | `COMPLETE_LOCAL` | Manual ✅ | — |
| Select topic/tag | Tag selection works | `DEMO_ONLY` | Manual ✅ | Tag model |
| Toggle privacy | Public/private switch | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap Create | Create room on server, navigate to LiveVoiceRoomScreen | `BROKEN` | ❌ | Room creation API |

---

## LudoScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Roll dice | Dice animation, valid move highlights | `COMPLETE_LOCAL` | Manual ✅ | — |
| Tap piece to move | Piece moves along board | `COMPLETE_LOCAL` | Manual ✅ | — |
| Capture opponent piece | Piece sent home | `COMPLETE_LOCAL` | Manual ✅ | — |
| Win condition | Victory screen shown | `COMPLETE_LOCAL` | Manual ✅ | — |
| Multiplayer over WebSocket | Real-time 2–4 players | `NOT_STARTED` | ❌ | Multiplayer backend |

---

## SpyScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Start game | Roles distributed (Spy vs Civilians) | `COMPLETE_LOCAL` | Manual ✅ | — |
| Reveal word | Word shown to civilian | `COMPLETE_LOCAL` | Manual ✅ | — |
| Vote phase | Players vote per turn | `COMPLETE_LOCAL` | Manual ✅ | — |
| Timer countdown | Timer ticks, round ends | `COMPLETE_LOCAL` | Manual ✅ | — |
| Win/lose result | Result shown | `COMPLETE_LOCAL` | Manual ✅ | — |
| Multiplayer | Real-time multi-device | `NOT_STARTED` | ❌ | Multiplayer backend |

---

## GiftsScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View gift grid | Gift tiles render with name/cost | `DEMO_ONLY` | Manual ✅ | Real gift catalog |
| Tap gift tile | Select gift; preview shown | `BROKEN` | ❌ | Selection logic not built |
| Adjust quantity | Quantity spinner updates total | `DEMO_ONLY` | Manual ✅ | — |
| Tap Send | Sends gift, deducts coins, fires animation | `BROKEN` | ❌ | GiftAnimationOverlay + API |
| Tier 1 animation | Small Lottie overlay | `NOT_STARTED` | ❌ | Lottie files + GiftAnimationOverlay |
| Tier 2 animation | Premium animation | `NOT_STARTED` | ❌ | Lottie files + GiftAnimationOverlay |
| Tier 3 animation | Full-screen luxury animation | `NOT_STARTED` | ❌ | Lottie files + GiftAnimationOverlay |

---

## FamilyScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View family info | Name/member count shown | `DEMO_ONLY` | Manual ✅ | FamilyModel + API |
| Join family | User joins family, confirmed | `DEMO_ONLY` | Manual ✅ | Family API |
| Leave family | User leaves family | `DEMO_ONLY` | Manual ✅ | Family API |
| View member roster | List of family members | `DEMO_ONLY` | Manual ✅ | Family API |
| Family XP / level | XP bar + level shown | `NOT_STARTED` | ❌ | FamilyModel |
| Family ranking | Family leaderboard | `NOT_STARTED` | ❌ | FamilyModel + API |
| Create family | Create flow | `NOT_STARTED` | ❌ | Family creation screen |

---

## BackpackScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View items | Item grid renders | `DEMO_ONLY` | Manual ✅ | Real item API |
| Equip item | Item equipped to profile | `DEMO_ONLY` | Manual ✅ | Item API |
| Unequip item | Item unequipped | `DEMO_ONLY` | Manual ✅ | Item API |

---

## TopUpScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| View coin packages | Package tiles render | `DEMO_ONLY` | Manual ✅ | Real billing |
| Tap Buy | Initiates Google Play purchase | `BROKEN` | ❌ | Google Play Billing v5 not integrated |
| Purchase success | Coins added to wallet | `NOT_STARTED` | ❌ | Billing + wallet API |

---

## ProfileEditScreen

| Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|
| Edit name | Updates UserProfile | `COMPLETE_LOCAL` | Manual ✅ | — |
| Edit bio | Updates UserProfile | `COMPLETE_LOCAL` | Manual ✅ | — |
| Change photo | Photo path updated | `COMPLETE_LOCAL` | Manual ✅ | — |
| Save changes | Writes to ProfileStore, navigates back | `COMPLETE_LOCAL` | Manual ✅ | — |

---

## Settings Screens

| Screen | Action | Expected Result | Current Status | Test Status | Blocker |
|---|---|---|---|---|---|
| SettingsHomeScreen | Tap any section row | Navigate to sub-screen | `COMPLETE_LOCAL` | Manual ✅ | — |
| AccountSettingsScreen | Edit display name | Updates profile | `COMPLETE_LOCAL` | Manual ✅ | — |
| AccountSettingsScreen | Logout | Clears session | `COMPLETE_LOCAL` | Manual ✅ | — |
| PrivacySettingsScreen | Toggle privacy switches | State saved locally | `COMPLETE_LOCAL` | Manual ✅ | Backend |
| BlockedUsersScreen | Tap Unblock | Removes from blocked list | `DEMO_ONLY` | Manual ✅ | Backend |
| ReportHistoryScreen | View reports | Demo list displayed | `DEMO_ONLY` | Manual ✅ | Backend |
| NotificationSettingsScreen | Toggle notifications | Preference saved | `COMPLETE_LOCAL` | Manual ✅ | FCM |
| DndScheduleScreen | Set DND times | Preference saved | `COMPLETE_LOCAL` | Manual ✅ | Backend sync |
| VoiceSettingsScreen | Toggle audio quality | Preference saved | `COMPLETE_LOCAL` | Manual ✅ | LiveKit API |
| GeneralSettingsScreen | Toggle theme | Live theme switch | `COMPLETE_LOCAL` | Manual ✅ | — |
| WalletSettingsScreen | View balance | Balance displayed | `DEMO_ONLY` | Manual ✅ | Wallet API |
| TransactionsScreen | View history | Demo list | `DEMO_ONLY` | Manual ✅ | Wallet API |
| PaymentMethodsScreen | Add payment method | Navigates to billing | `BROKEN` | ❌ | Billing API |
| HostEarningsScreen | View earnings | Demo summary | `DEMO_ONLY` | Manual ✅ | Backend |
| SupportSettingsScreen | Tap FAQ / Contact | Opens support flow | `DEMO_ONLY` | Manual ✅ | Support API |
| AboutScreen | View version | Version string shown | `COMPLETE_LOCAL` | Manual ✅ | — |
| MeSettingsScreen | Navigate sub-settings | Routes correctly | `COMPLETE_LOCAL` | Manual ✅ | — |

---

## Summary Counts

| Status | Count |
|---|---|
| `COMPLETE_LOCAL` | 52 |
| `COMPLETE_LIVE` | 4 |
| `DEMO_ONLY` | 28 |
| `BLOCKED_INTEGRATION` | 1 |
| `BROKEN` | 18 |
| `NOT_STARTED` | 32 |
| **Total actions tracked** | **135** |
