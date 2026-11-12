package me.rexsystems.rexSellwands.sell;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

public final class Containers {

    private Containers() {
    }

    public static Inventory of(Player player, Block block) {
        if (block == null) {
            return null;
        }
        if (block.getType() == Material.ENDER_CHEST) {
            return player == null ? null : player.getEnderChest();
        }
        BlockState state = block.getState();
        if (state instanceof Chest chest) {
            // Both halves. getBlockInventory() would be one side and could pay twice.
            return chest.getInventory();
        }
        if (state instanceof InventoryHolder holder) {
            return holder.getInventory();
        }
        return null;
    }

    public static ItemStack[] copy(Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        ItemStack[] snapshot = new ItemStack[contents.length];
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            snapshot[slot] = stack == null ? null : stack.clone();
        }
        return snapshot;
    }

    public static List<ItemLine> read(ItemStack[] contents) {
        List<ItemLine> lines = new ArrayList<>();
        if (contents == null) {
            return lines;
        }
        for (int slot = 0; slot < contents.length; slot++) {
            ItemLine line = readOne(slot, contents[slot]);
            if (line != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static ItemLine readOne(int slot, ItemStack stack) {
        if (stack == null || stack.getType().isAir() || stack.getAmount() <= 0) {
            return null;
        }
        List<ItemLine> inner = List.of();
        if (isShulker(stack.getType()) && stack.getItemMeta() instanceof BlockStateMeta meta
                && meta.getBlockState() instanceof ShulkerBox box) {
            inner = read(box.getInventory().getContents());
        }
        return new ItemLine(slot, stack.getType().name(), stack.getAmount(), inner);
    }

    public static boolean isShulker(Material type) {
        if (type == null) {
            return false;
        }
        String name = type.name();
        return name.equals("SHULKER_BOX") || name.endsWith("_SHULKER_BOX");
    }
}
