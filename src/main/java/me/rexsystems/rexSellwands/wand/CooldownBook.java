package me.rexsystems.rexSellwands.wand;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CooldownBook {

    private final Map<UUID, Map<String, Long>> until = new ConcurrentHashMap<>();

    public long remainingMillis(UUID player, String wandId) {
        if (player == null || wandId == null) {
            return 0;
        }
        Map<String, Long> playerCooldowns = until.get(player);
        if (playerCooldowns == null) {
            return 0;
        }
        Long expiry = playerCooldowns.get(wandId);
        if (expiry == null) {
            return 0;
        }
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            playerCooldowns.remove(wandId);
            return 0;
        }
        return remaining;
    }

    public void start(UUID player, String wandId, int seconds) {
        if (player == null || wandId == null || seconds <= 0) {
            return;
        }
        until.computeIfAbsent(player, id -> new ConcurrentHashMap<>())
                .put(wandId, System.currentTimeMillis() + seconds * 1000L);
    }
}
