import { RoomThemesError } from "./service.mjs";

const BASE = "/room-themes";

export async function handleRoomThemesRequest({ service, method, path, userId, query = {}, body = null }) {
  try {
    if (typeof userId !== "string" || userId.length === 0) {
      throw new RoomThemesError("unauthenticated", "Authentication required.", 401);
    }

    const route = normalize(path);
    const verb = String(method ?? "").toUpperCase();

    if (verb === "GET" && route === `${BASE}/catalog`) {
      if (!query.roomId) throw new RoomThemesError("invalid_room", "roomId query parameter is required.");
      return { status: 200, body: await service.listCatalogForRoom({ roomId: query.roomId, userId }) };
    }
    if (verb === "GET" && route === `${BASE}/crest`) {
      if (!query.roomId) throw new RoomThemesError("invalid_room", "roomId query parameter is required.");
      return { status: 200, body: await service.getRoomCrest(query.roomId) };
    }
    if (verb === "POST" && route === `${BASE}/equip`) {
      const outcome = await service.equipRoomTheme({
        roomId: body?.roomId,
        userId,
        themeId: body?.themeId,
      });
      return { status: 200, body: outcome };
    }
    if (verb === "POST" && route === `${BASE}/purchase`) {
      const outcome = await service.purchaseStoreTheme({
        userId,
        themeId: body?.themeId,
        idempotencyKey: body?.idempotencyKey,
      });
      return { status: outcome.replay ? 200 : 201, body: outcome };
    }
    if (verb === "POST" && route === `${BASE}/crest/events`) {
      const outcome = await service.recordCrestEvent({
        roomId: body?.roomId,
        actorId: body?.actorId ?? userId,
        eventType: body?.eventType,
        requestedXp: body?.requestedXp,
        idempotencyKey: body?.idempotencyKey,
      });
      return { status: outcome.replay ? 200 : 201, body: outcome };
    }

    throw new RoomThemesError("not_found", "Unknown room-themes route.", 404);
  } catch (error) {
    if (error instanceof RoomThemesError) {
      return { status: error.status, body: { error: { code: error.code, message: error.message } } };
    }
    throw error;
  }
}

function normalize(path) {
  const value = String(path ?? "");
  if (value.length > 1 && value.endsWith("/")) return value.slice(0, -1);
  return value;
}
