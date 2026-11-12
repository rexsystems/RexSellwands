package me.rexsystems.rexSellwands.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public final class WandRegistry {

    private static final Pattern ID = Pattern.compile("[a-z0-9_-]{1,48}");
    private static final List<String> DEFAULT_CONTAINERS = List.of(
            "CHEST", "BARREL", "ENDER_CHEST", "TRAPPED_CHEST", "SHULKER_BOX"
    );

    private final Map<String, WandDefinition> wands;

    private WandRegistry(Map<String, WandDefinition> wands) {
        this.wands = Map.copyOf(wands);
    }

    public static WandRegistry read(FileConfiguration yaml, Logger logger) {
        Map<String, WandDefinition> parsed = new LinkedHashMap<>();
        ConfigurationSection section = yaml == null ? null : yaml.getConfigurationSection("wands");
        if (section == null) {
            return new WandRegistry(parsed);
        }
        for (String key : section.getKeys(false)) {
            WandDefinition wand = readOne(key, section.getConfigurationSection(key), logger);
            if (wand != null) {
                parsed.put(wand.id(), wand);
            }
        }
        return new WandRegistry(parsed);
    }

    private static WandDefinition readOne(String key, ConfigurationSection section, Logger logger) {
        if (key == null || section == null) {
            return null;
        }
        String id = key.toLowerCase(Locale.ROOT);
        if (!ID.matcher(id).matches()) {
            logger.warning("Skipping wand " + key + ". Ids use letters, numbers, underscores, and hyphens.");
            return null;
        }
        String materialName = section.getString("material", "");
        Material material = Material.matchMaterial(materialName == null ? "" : materialName);
        if (material == null || !material.isItem() || material.isAir()) {
            logger.warning("Skipping wand " + id + ". Material is missing or not an item.");
            return null;
        }
        List<String> containers = section.getStringList("allowed-containers");
        if (containers == null || containers.isEmpty()) {
            containers = DEFAULT_CONTAINERS;
        }
        Set<String> allowed = new HashSet<>();
        for (String container : containers) {
            if (container != null && !container.isBlank()) {
                allowed.add(container.trim().toUpperCase(Locale.ROOT));
            }
        }
        List<String> lore = section.getStringList("lore");
        return new WandDefinition(
                id,
                material,
                section.getString("name", id),
                lore == null ? List.of() : lore,
                section.getInt("custom-model-data", 0),
                section.getBoolean("glow", false),
                section.getDouble("multiplier", 1.0),
                section.getInt("uses", 0),
                section.getBoolean("unbreakable", false),
                section.getInt("cooldown-seconds", 0),
                allowed,
                section.getString("particles", ""),
                section.getString("sound", ""),
                section.getString("hologram", "")
        );
    }

    public WandDefinition get(String id) {
        if (id == null) {
            return null;
        }
        return wands.get(id.toLowerCase(Locale.ROOT));
    }

    public List<WandDefinition> all() {
        return new ArrayList<>(wands.values());
    }

    public boolean isEmpty() {
        return wands.isEmpty();
    }

    public static WandRegistry empty() {
        return new WandRegistry(Map.of());
    }
}
