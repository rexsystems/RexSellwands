package me.rexsystems.rexSellwands.sell;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.WandDefinition;
import me.rexsystems.rexSellwands.util.MoneyFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SaleService {

    private final RexSellwands plugin;

    public SaleService(RexSellwands plugin) {
        this.plugin = plugin;
    }

    public void preview(Player player, WandDefinition wand, Inventory inventory) {
        Prepared prepared = prepare(player, wand, inventory);
        if (!prepared.quote.soldAnything()) {
            plugin.getPluginConfig().send(player, "nothing-to-sell", Map.of());
            sendUnsellable(player, prepared.quote);
            return;
        }
        plugin.getPluginConfig().send(player, "preview", tokens(prepared.quote, wand));
        sendUnsellable(player, prepared.quote);
    }

    public void sell(Player player, WandDefinition wand, ItemStack item, EquipmentSlot hand, Block block, Inventory inventory) {
        long remaining = plugin.getCooldowns().remainingMillis(player.getUniqueId(), wand.id());
        if (remaining > 0) {
            long seconds = Math.max(1L, (remaining + 999L) / 1000L);
            plugin.getPluginConfig().send(player, "cooldown", Map.of("seconds", Long.toString(seconds)));
            return;
        }

        Prepared prepared = prepare(player, wand, inventory);
        if (!prepared.quote.soldAnything()) {
            plugin.getPluginConfig().send(player, "nothing-to-sell", Map.of());
            sendUnsellable(player, prepared.quote);
            return;
        }

        // Take the sold stacks out first. Pay only after that. Put the snapshot back if payment fails.
        apply(inventory, prepared.lines, prepared.quote);
        boolean deposited = false;
        try {
            deposited = plugin.getPluginConfig().currency().deposit(player, prepared.quote.payout());
        } catch (Throwable failure) {
            plugin.getLogger().warning("Deposit failed: " + failure.getMessage());
        }
        Settlement settlement = SaleSettlement.settle(prepared.lines, prepared.quote, deposited);
        if (settlement.restored()) {
            inventory.setContents(prepared.snapshot);
            plugin.getPluginConfig().send(player, "deposit-failed", Map.of());
            return;
        }
        if (!settlement.decrementUses()) {
            return;
        }

        ItemStack updated = plugin.getWandItems().afterUse(item);
        setHand(player, hand, updated);
        if (updated == null) {
            plugin.getPluginConfig().send(player, "wand-broke", Map.of());
        }
        plugin.getCooldowns().start(player.getUniqueId(), wand.id(), wand.cooldownSeconds());
        Map<String, String> tokens = tokens(prepared.quote, wand);
        plugin.getPluginConfig().send(player, "sold", tokens);
        sendUnsellable(player, prepared.quote);
        plugin.getFeedback().success(player, block.getLocation(), wand, tokens);
    }

    private Prepared prepare(Player player, WandDefinition wand, Inventory inventory) {
        double multiplier = Multipliers.resolve(granted(player), wand.id(), wand.multiplier());
        ItemStack[] snapshot = Containers.copy(inventory);
        List<ItemLine> lines = Containers.read(snapshot);
        SaleQuote quote = SaleMath.quote(
                lines,
                plugin.getPluginConfig().prices(),
                plugin.getPluginConfig().sellShulkerContents(),
                multiplier
        );
        return new Prepared(snapshot, lines, quote);
    }

    private void apply(Inventory inventory, List<ItemLine> lines, SaleQuote quote) {
        Map<Integer, ItemLine> kept = new HashMap<>();
        for (ItemLine line : quote.remaining()) {
            kept.put(line.slot(), line);
        }
        for (ItemLine line : lines) {
            ItemLine stay = kept.get(line.slot());
            if (stay == null) {
                inventory.setItem(line.slot(), null);
                continue;
            }
            if (!line.contents().equals(stay.contents())) {
                ItemStack current = inventory.getItem(line.slot());
                if (current != null) {
                    inventory.setItem(line.slot(), ShulkerContents.rewrite(current, stay));
                }
            }
        }
    }

    private void sendUnsellable(Player player, SaleQuote quote) {
        String items = MaterialNames.join(quote.unsellable());
        if (items.isBlank()) {
            return;
        }
        plugin.getPluginConfig().send(player, "unsellable", Map.of("items", items));
    }

    private Map<String, String> tokens(SaleQuote quote, WandDefinition wand) {
        return Map.of(
                "price", MoneyFormat.format(quote.payout(), plugin.getPluginConfig().moneyFormat()),
                "wand", wand.id(),
                "items", MaterialNames.join(quote.unsellable()),
                "multiplier", MoneyFormat.compact(wand.multiplier())
        );
    }

    private static List<String> granted(Player player) {
        List<String> nodes = new ArrayList<>();
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getValue()) {
                nodes.add(info.getPermission());
            }
        }
        return nodes;
    }

    private static void setHand(Player player, EquipmentSlot hand, ItemStack stack) {
        ItemStack next = stack == null ? new ItemStack(Material.AIR) : stack;
        if (hand == EquipmentSlot.OFF_HAND) {
            player.getInventory().setItemInOffHand(next);
            return;
        }
        player.getInventory().setItemInMainHand(next);
    }

    private record Prepared(ItemStack[] snapshot, List<ItemLine> lines, SaleQuote quote) {
    }
}
