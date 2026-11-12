package me.rexsystems.rexSellwands.sell;

import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.HashMap;
import java.util.Map;

public final class ShulkerContents {

    private ShulkerContents() {
    }

    public static ItemStack rewrite(ItemStack source, ItemLine kept) {
        if (source == null || kept == null) {
            return source;
        }
        ItemStack copy = source.clone();
        if (!(copy.getItemMeta() instanceof BlockStateMeta meta) || !(meta.getBlockState() instanceof ShulkerBox box)) {
            return copy;
        }
        Inventory inner = box.getInventory();
        ItemStack[] contents = inner.getContents();
        Map<Integer, ItemLine> children = new HashMap<>();
        for (ItemLine child : kept.contents()) {
            children.put(child.slot(), child);
        }
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack stack = contents[slot];
            if (stack == null || stack.getType().isAir()) {
                continue;
            }
            ItemLine child = children.get(slot);
            if (child == null) {
                inner.setItem(slot, null);
                continue;
            }
            ItemLine original = Containers.readOne(slot, stack);
            if (original != null && !original.contents().equals(child.contents())) {
                inner.setItem(slot, rewrite(stack, child));
            }
        }
        meta.setBlockState(box);
        copy.setItemMeta(meta);
        return copy;
    }
}
