package me.rexsystems.rexSellwands.command;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.WandDefinition;
import me.rexsystems.rexSellwands.util.MoneyFormat;
import me.rexsystems.rexSellwands.util.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class SellWandsCommand implements CommandExecutor, TabCompleter {

    private final RexSellwands plugin;

    public SellWandsCommand(RexSellwands plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            return help(sender);
        }
        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> reload(sender);
            case "give" -> give(sender, args);
            case "list" -> list(sender);
            default -> help(sender);
        };
    }

    private boolean help(CommandSender sender) {
        plugin.getPluginConfig().send(sender, "help-header", Map.of());
        plugin.getPluginConfig().sendLines(sender, "help");
        return true;
    }

    private boolean reload(CommandSender sender) {
        if (!sender.hasPermission("rexsellwands.reload")) {
            plugin.getPluginConfig().send(sender, "no-permission", Map.of());
            return true;
        }
        plugin.reloadPlugin();
        plugin.getPluginConfig().send(sender, "reload", Map.of());
        return true;
    }

    private boolean list(CommandSender sender) {
        if (plugin.getPluginConfig().wands().isEmpty()) {
            plugin.getPluginConfig().send(sender, "list-empty", Map.of());
            return true;
        }
        plugin.getPluginConfig().send(sender, "list-header", Map.of());
        for (WandDefinition wand : plugin.getPluginConfig().wands().all()) {
            plugin.getPluginConfig().send(sender, "list-line", Map.of(
                    "wand", wand.id(),
                    "material", wand.material().name(),
                    "multiplier", MoneyFormat.compact(wand.multiplier()),
                    "uses", wand.uses() <= 0 ? "Infinite" : Integer.toString(wand.uses())
            ));
        }
        return true;
    }

    private boolean give(CommandSender sender, String[] args) {
        if (!sender.hasPermission("rexsellwands.give")) {
            plugin.getPluginConfig().send(sender, "no-permission", Map.of());
            return true;
        }
        if (args.length < 3) {
            plugin.getPluginConfig().send(sender, "give-usage", Map.of());
            return true;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            plugin.getPluginConfig().send(sender, "unknown-player", Map.of("player", args[1]));
            return true;
        }
        WandDefinition wand = plugin.getPluginConfig().wand(args[2]);
        if (wand == null) {
            plugin.getPluginConfig().send(sender, "unknown-wand", Map.of("wand", args[2]));
            return true;
        }
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException ex) {
                amount = -1;
            }
        }
        if (amount < 1 || amount > 64) {
            plugin.getPluginConfig().send(sender, "invalid-amount", Map.of());
            return true;
        }
        int giving = amount;
        SchedulerUtils.runForPlayer(plugin, target, () -> deliver(sender, target, wand, giving));
        return true;
    }

    private void deliver(CommandSender sender, Player target, WandDefinition wand, int amount) {
        if (!target.isOnline()) {
            plugin.getPluginConfig().send(sender, "unknown-player", Map.of("player", target.getName()));
            return;
        }
        List<ItemStack> items = plugin.getWandItems().bundle(wand, amount);
        boolean dropped = false;
        for (ItemStack stack : items) {
            Map<Integer, ItemStack> leftover = target.getInventory().addItem(stack);
            for (ItemStack extra : leftover.values()) {
                target.getWorld().dropItemNaturally(target.getLocation(), extra);
                dropped = true;
            }
        }
        Map<String, String> tokens = Map.of(
                "amount", Integer.toString(amount),
                "wand", wand.id(),
                "player", target.getName()
        );
        plugin.getPluginConfig().send(sender, "give", tokens);
        plugin.getPluginConfig().send(target, "received", tokens);
        if (dropped) {
            plugin.getPluginConfig().send(target, "inventory-full", Map.of());
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("help");
            options.add("list");
            if (sender.hasPermission("rexsellwands.give")) {
                options.add("give");
            }
            if (sender.hasPermission("rexsellwands.reload")) {
                options.add("reload");
            }
            return filter(options, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("rexsellwands.give")) {
            List<String> names = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                names.add(player.getName());
            }
            return filter(names, args[1]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give") && sender.hasPermission("rexsellwands.give")) {
            List<String> ids = new ArrayList<>();
            for (WandDefinition wand : plugin.getPluginConfig().wands().all()) {
                ids.add(wand.id());
            }
            return filter(ids, args[2]);
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("give") && sender.hasPermission("rexsellwands.give")) {
            return filter(List.of("1", "16", "64"), args[3]);
        }
        return List.of();
    }

    private static List<String> filter(List<String> options, String prefix) {
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(needle)) {
                matches.add(option);
            }
        }
        return matches;
    }
}
