package me.rexsystems.rexSellwands.sell;

import java.util.List;
import java.util.Locale;

public record ItemLine(int slot, String material, int amount, List<ItemLine> contents) {

    public ItemLine {
        material = material == null ? "" : material.toUpperCase(Locale.ROOT);
        amount = Math.max(0, amount);
        contents = contents == null ? List.of() : List.copyOf(contents);
    }

    public boolean shulker() {
        return material.equals("SHULKER_BOX") || material.endsWith("_SHULKER_BOX");
    }

    public ItemLine withContents(List<ItemLine> next) {
        return new ItemLine(slot, material, amount, next);
    }
}
