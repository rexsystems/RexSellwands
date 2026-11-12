package me.rexsystems.rexSellwands.sell;

import java.util.Collection;
import java.util.Locale;

public final class Multipliers {

    private Multipliers() {
    }

    public static double resolve(Collection<String> granted, String wandId, double base) {
        if (!Double.isFinite(base)) {
            base = 1;
        }
        if (wandId == null || wandId.isBlank() || granted == null || granted.isEmpty()) {
            return base;
        }
        String prefix = "rexsellwands.multiplier." + wandId.toLowerCase(Locale.ROOT) + ".";
        Double best = null;
        for (String permission : granted) {
            if (permission == null) {
                continue;
            }
            String node = permission.toLowerCase(Locale.ROOT);
            if (!node.startsWith(prefix)) {
                continue;
            }
            String tail = node.substring(prefix.length());
            if (tail.isEmpty()) {
                continue;
            }
            try {
                double value = Double.parseDouble(tail);
                if (!Double.isFinite(value)) {
                    continue;
                }
                if (best == null || value > best) {
                    best = value;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return best == null ? base : best;
    }
}
