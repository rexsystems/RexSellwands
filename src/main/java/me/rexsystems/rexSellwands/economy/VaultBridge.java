package me.rexsystems.rexSellwands.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

final class VaultBridge {

    private VaultBridge() {
    }

    static boolean available() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        return provider() != null;
    }

    static boolean deposit(Player player, double amount) {
        return call(player, amount, "depositPlayer");
    }

    static boolean withdraw(Player player, double amount) {
        return call(player, amount, "withdrawPlayer");
    }

    private static boolean call(Player player, double amount, String method) {
        Object economy = provider();
        if (economy == null || player == null) {
            return false;
        }
        try {
            Object response = economy.getClass()
                    .getMethod(method, OfflinePlayer.class, double.class)
                    .invoke(economy, player, amount);
            if (response == null) {
                return false;
            }
            Object success = response.getClass().getMethod("transactionSuccess").invoke(response);
            return Boolean.TRUE.equals(success);
        } catch (ReflectiveOperationException offlineMissing) {
            try {
                Object response = economy.getClass()
                        .getMethod(method, String.class, double.class)
                        .invoke(economy, player.getName(), amount);
                if (response == null) {
                    return false;
                }
                Object success = response.getClass().getMethod("transactionSuccess").invoke(response);
                return Boolean.TRUE.equals(success);
            } catch (ReflectiveOperationException failure) {
                return false;
            }
        }
    }

    private static Object provider() {
        try {
            Class<?> economyClass = Class.forName("net.milkbowl.vault.economy.Economy");
            Object registration = Bukkit.getServicesManager().getRegistration(economyClass);
            if (registration == null) {
                return null;
            }
            return registration.getClass().getMethod("getProvider").invoke(registration);
        } catch (ReflectiveOperationException failure) {
            return null;
        }
    }
}
