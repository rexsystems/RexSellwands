package me.rexsystems.rexSellwands.config;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.economy.Currency;
import me.rexsystems.rexSellwands.economy.Currencies;
import me.rexsystems.rexSellwands.sell.PriceBook;
import me.rexsystems.rexSellwands.util.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class PluginConfig {

    private final RexSellwands plugin;
    private FileConfiguration config;
    private WandRegistry wands = WandRegistry.empty();
    private PriceBook prices = new PriceBook(Map.of(), Set.of());
    private Currency currency = Currency.NONE;

    public PluginConfig(RexSellwands plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Could not create the plugin folder.");
        }
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        new ConfigAutoUpdater(plugin).update();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        ensureResource("wands.yml");
        ensureResource("prices.yml");
        this.wands = WandRegistry.read(loadYaml("wands.yml"), plugin.getLogger());
        this.prices = readPrices(loadYaml("prices.yml"));
        this.currency = Currencies.select(plugin, this);
    }

    private void ensureResource(String name) {
        File file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.saveResource(name, false);
        }
    }

    private FileConfiguration loadYaml(String name) {
        File file = new File(plugin.getDataFolder(), name);
        return YamlConfiguration.loadConfiguration(file);
    }

    private PriceBook readPrices(FileConfiguration yaml) {
        Map<String, Double> values = new HashMap<>();
        ConfigurationSection section = yaml.getConfigurationSection("prices");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (key == null || !section.isSet(key) || section.isConfigurationSection(key)) {
                    continue;
                }
                double price = section.getDouble(key);
                if (!Double.isFinite(price)) {
                    continue;
                }
                values.put(key.toUpperCase(Locale.ROOT), price);
            }
        }
        Set<String> blacklist = new HashSet<>();
        List<String> blocked = yaml.getStringList("blacklist");
        if (blocked != null) {
            for (String name : blocked) {
                if (name != null && !name.isBlank()) {
                    blacklist.add(name.toUpperCase(Locale.ROOT));
                }
            }
        }
        return new PriceBook(values, blacklist);
    }

    public WandDefinition wand(String id) {
        return wands.get(id);
    }

    public WandRegistry wands() {
        return wands;
    }

    public PriceBook prices() {
        return prices;
    }

    public Currency currency() {
        return currency;
    }

    public boolean sellShulkerContents() {
        return config.getBoolean("settings.sell-shulker-contents", false);
    }

    public double hologramSeconds() {
        double seconds = config.getDouble("settings.hologram-seconds", 3);
        if (!Double.isFinite(seconds) || seconds <= 0) {
            return 0;
        }
        return seconds;
    }

    public String moneyFormat() {
        String pattern = config.getString("settings.money-format", "#,##0.##");
        return pattern == null || pattern.isBlank() ? "#,##0.##" : pattern;
    }

    public String currencyType() {
        String type = config.getString("currency.type", "vault");
        if (type == null || type.isBlank()) {
            return "vault";
        }
        return type.toLowerCase(Locale.ROOT);
    }

    public String balancePlaceholder() {
        return config.getString("currency.balance-placeholder", "");
    }

    public String giveCommand() {
        return config.getString("currency.give-command", "");
    }

    public String takeCommand() {
        return config.getString("currency.take-command", "");
    }

    public String prefix() {
        String prefix = config.getString("messages.prefix", "");
        return prefix == null ? "" : prefix;
    }

    public String prefixed(String path) {
        String raw = config.getString("messages." + path, "");
        if (raw == null) {
            return "";
        }
        return raw.replace("{prefix}", prefix()).replace("<prefix>", prefix());
    }

    public void send(CommandSender sender, String path, Map<String, String> tokens) {
        Audience.send(sender, prefixed(path), tokens);
    }

    public void sendLines(CommandSender sender, String path) {
        List<String> lines = config.getStringList("messages." + path);
        if (lines == null) {
            return;
        }
        String prefix = prefix();
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            Audience.send(sender, line.replace("{prefix}", prefix).replace("<prefix>", prefix), Map.of());
        }
    }
}
