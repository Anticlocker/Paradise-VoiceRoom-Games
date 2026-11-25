import test from "node:test";
import assert from "node:assert/strict";
import { readFileSync } from "node:fs";
import { resolve } from "node:path";
import { ROOM_THEME_CATALOG } from "./catalog.mjs";
import {
  DAILY_CREST_XP_CAP,
  crestLevelForXp,
  evaluateCrestEventAntiFarming,
  validateThemePurchaseEligibility,
  xpForCrestLevel,
} from "./service.mjs";

test("server room theme catalog matches Android app asset JSON exactly", () => {
  const assetPath = resolve(import.meta.dirname, "../../app/src/main/assets/room_theme_catalog.json");
  const androidCatalog = JSON.parse(readFileSync(assetPath, "utf8"));
  assert.equal(ROOM_THEME_CATALOG.length, 10);
  assert.deepEqual(ROOM_THEME_CATALOG, androidCatalog);
});

test("classic_lounge is the only default equipped free theme", () => {
  const defaults = ROOM_THEME_CATALOG.filter((t) => t.defaultEquipped);
  assert.equal(defaults.length, 1);
  assert.equal(defaults[0].id, "classic_lounge");
  assert.equal(defaults[0].unlockType, "free");
});

test("store theme purchase rejects null priceCoins as unavailable (never 0 coins)", () => {
  const sakura = ROOM_THEME_CATALOG.find((t) => t.id === "sakura_garden");
  assert.equal(sakura.priceCoins, null);
  assert.throws(
    () => validateThemePurchaseEligibility(sakura, false),
    { code: "price_not_configured", status: 409 },
  );

  // Configured positive price passes validation cleanly
  const priced = { ...sakura, price_coins: 999 };
  assert.deepEqual(validateThemePurchaseEligibility(priced, false), { alreadyOwned: false, price: 999 });
});

test("crest XP thresholds and anti-farming rules reject self-gifts and cap daily XP", () => {
  assert.equal(crestLevelForXp(0), 1);
  assert.equal(crestLevelForXp(xpForCrestLevel(3)), 3);
  assert.equal(crestLevelForXp(xpForCrestLevel(9)), 9);
  assert.equal(crestLevelForXp(xpForCrestLevel(21)), 21);

  const selfGift = evaluateCrestEventAntiFarming({
    eventType: "gift_value_received",
    actorId: "host_1",
    hostId: "host_1",
    requestedXp: 100,
    dailyXpEarned: 0,
  });
  assert.equal(selfGift.accepted, false);
  assert.equal(selfGift.awardedXp, 0);

  const capped = evaluateCrestEventAntiFarming({
    eventType: "host_session_minute",
    actorId: "host_1",
    hostId: "host_1",
    requestedXp: 250,
    dailyXpEarned: DAILY_CREST_XP_CAP - 40,
  });
  assert.equal(capped.accepted, true);
  assert.equal(capped.awardedXp, 40);
});
