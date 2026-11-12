package me.rexsystems.rexSellwands.sell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PriceBook {

    private final Map<String, Double> prices;
    private final Set<String> blacklist;

    public PriceBook(Map<String, Double> prices, Set<String> blacklist) {
        Map<String, Double> copy = new HashMap<>();
        if (prices != null) {
            for (Map.Entry<String, Double> entry : prices.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null || !Double.isFinite(entry.getValue())) {
                    continue;
                }
                copy.put(entry.getKey().toUpperCase(Locale.ROOT), entry.getValue());
            }
        }
        this.prices = Map.copyOf(copy);

        Set<String> blocked = new HashSet<>();
        if (blacklist != null) {
            for (String name : blacklist) {
                if (name != null && !name.isBlank()) {
                    blocked.add(name.toUpperCase(Locale.ROOT));
                }
            }
        }
        this.blacklist = Set.copyOf(blocked);
    }

    public boolean blacklisted(String material) {
        return material != null && blacklist.contains(material.toUpperCase(Locale.ROOT));
    }

    public Double price(String material) {
        if (material == null) {
            return null;
        }
        return prices.get(material.toUpperCase(Locale.ROOT));
    }

    public Set<String> materials() {
        return prices.keySet();
    }
}
