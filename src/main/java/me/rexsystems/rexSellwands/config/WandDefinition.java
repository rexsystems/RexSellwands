package me.rexsystems.rexSellwands.config;

import org.bukkit.Material;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public record WandDefinition(
        String id,
        Material material,
        String name,
        List<String> lore,
        int customModelData,
        boolean glow,
        double multiplier,
        int uses,
        boolean unbreakable,
        int cooldownSeconds,
        Set<String> containers,
        String particles,
        String sound,
        String hologram
) {

    public WandDefinition {
        id = id == null ? "" : id.toLowerCase(Locale.ROOT);
        name = name == null ? "" : name;
        lore = lore == null ? List.of() : List.copyOf(lore);
        containers = containers == null ? Set.of() : Set.copyOf(containers);
        particles = particles == null ? "" : particles;
        sound = sound == null ? "" : sound;
        hologram = hologram == null ? "" : hologram;
        if (!Double.isFinite(multiplier) || multiplier < 0) {
            multiplier = 1;
        }
        if (uses < 0) {
            uses = 0;
        }
        if (cooldownSeconds < 0) {
            cooldownSeconds = 0;
        }
        if (customModelData < 0) {
            customModelData = 0;
        }
    }

    public boolean allows(Material type) {
        if (type == null) {
            return false;
        }
        String name = type.name();
        if (containers.contains(name)) {
            return true;
        }
        return (name.equals("SHULKER_BOX") || name.endsWith("_SHULKER_BOX")) && containers.contains("SHULKER_BOX");
    }
}
