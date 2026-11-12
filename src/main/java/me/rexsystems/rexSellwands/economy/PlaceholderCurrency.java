package me.rexsystems.rexSellwands.economy;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.util.MoneyFormat;
import me.rexsystems.rexSellwands.util.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderCurrency implements Currency {

    private final RexSellwands plugin;

    public PlaceholderCurrency(RexSellwands plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean deposit(Player player, double amount) {
        if (player == null || !Double.isFinite(amount) || amount <= 0) {
            return false;
        }
        String command = fill(plugin.getPluginConfig().giveCommand(), player, amount);
        if (command.isBlank()) {
            return false;
        }
        Double before = read(player);
        boolean dispatched = dispatch(command);
        Double after = read(player);
        if (before == null || after == null) {
            return dispatched;
        }
        if (after + 0.001d >= before + amount - 0.01d) {
            return true;
        }
        if (dispatched) {
            withdraw(player, amount);
        }
        return false;
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        if (player == null || !Double.isFinite(amount) || amount <= 0) {
            return false;
        }
        String command = fill(plugin.getPluginConfig().takeCommand(), player, amount);
        if (command.isBlank()) {
            return false;
        }
        return dispatch(command);
    }

    private Double read(Player player) {
        String placeholder = plugin.getPluginConfig().balancePlaceholder();
        if (placeholder == null || placeholder.isBlank()) {
            return null;
        }
        return MoneyFormat.parse(Placeholders.apply(player, placeholder));
    }

    private static boolean dispatch(String command) {
        try {
            return Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } catch (Throwable failure) {
            return false;
        }
    }

    static String fill(String template, Player player, double amount) {
        if (template == null) {
            return "";
        }
        String command = template.trim();
        if (command.startsWith("/")) {
            command = command.substring(1).trim();
        }
        return command
                .replace("{player}", player.getName())
                .replace("{amount}", MoneyFormat.commandAmount(amount));
    }
}
