package me.rexsystems.rexSellwands.economy;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.PluginConfig;
import org.bukkit.entity.Player;

public final class Currencies {

    private Currencies() {
    }

    public static Currency select(RexSellwands plugin) {
        return select(plugin, plugin.getPluginConfig());
    }

    public static Currency select(RexSellwands plugin, PluginConfig config) {
        String type = config.currencyType();
        if ("placeholder".equals(type)) {
            return new PlaceholderCurrency(plugin);
        }
        if (!"vault".equals(type)) {
            plugin.getLogger().warning("Unknown currency type " + type + ". Using vault.");
        }
        if (!VaultBridge.available()) {
            plugin.getLogger().warning("Vault economy is not available. Sales put items back until it is.");
            return Currency.NONE;
        }
        return new Currency() {
            @Override
            public boolean deposit(Player player, double amount) {
                if (player == null || !Double.isFinite(amount) || amount <= 0) {
                    return false;
                }
                return VaultBridge.deposit(player, amount);
            }

            @Override
            public boolean withdraw(Player player, double amount) {
                if (player == null || !Double.isFinite(amount) || amount <= 0) {
                    return false;
                }
                return VaultBridge.withdraw(player, amount);
            }
        };
    }
}
