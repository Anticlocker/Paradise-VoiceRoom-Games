# Discover UI + functionality acceptance checklist

## Theme
- [ ] Moonlit palace header appears without status bar duplication or black top gap.
- [ ] White/lavender text contrast verified against dark card surfaces.
- [ ] Approved section ordering and compact vertical rhythm; no oversized cards.
- [ ] Four shortcuts visible/scrollable without clipped names at 360dp and large fonts.
- [ ] Shared bottom dock remains fixed, no duplicate navbar or intercepted clicks.

## Functional
- [ ] Community Moments 'More' opens real post list or honest unavailable state.
- [ ] Media cards come from actual posts; privacy respected.
- [ ] Families opens correct existing route.
- [ ] Nearby prompts for location only on explicit action; denial/retry works.
- [ ] Clubs & Spaces has working route/data or clear unavailable state.
- [ ] Live Events opens actual events route; no fake live status.
- [ ] Activity preview is accurate, not unmarked demo data.
- [ ] Posts can be opened; photo attachments post correctly when supported.
- [ ] Like/comment/share actions work where available; no fake success.
- [ ] Search/tag pills filter when supported or disabled clearly.
- [ ] All bottom tabs and back navigation function after visiting Discover subroutes.

## Verification
- [ ] Baseline assembleDebug, testDebugUnitTest, lintDebug recorded.
- [ ] Final assembleDebug, testDebugUnitTest, lintDebug recorded.
- [ ] 3 actual Android screenshots taken; compare to approved reference.
- [ ] No test marked PASS without execution evidence.
- [ ] Check 360dp viewport, normal 393dp viewport, font scaling, TalkBack.
- [ ] Report identifies all DEMO_ONLY features and unimplemented APIs.
