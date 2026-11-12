package me.rexsystems.rexSellwands.sell;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class MaterialNames {

    private MaterialNames() {
    }

    public static String join(List<String> materials) {
        if (materials == null || materials.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        Set<String> seen = new HashSet<>();
        for (String material : materials) {
            if (material == null || material.isBlank() || !seen.add(material)) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(material.toLowerCase(Locale.ROOT).replace('_', ' '));
        }
        return builder.toString();
    }
}
