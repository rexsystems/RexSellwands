package me.rexsystems.rexSellwands.wand;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.WandDefinition;
import me.rexsystems.rexSellwands.util.MoneyFormat;
import me.rexsystems.rexSellwands.util.TextParser;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class WandItems {

    private final RexSellwands plugin;
    private final NamespacedKey idKey;
    private final NamespacedKey usesKey;

    public WandItems(RexSellwands plugin) {
        this.plugin = plugin;
        this.idKey = new NamespacedKey(plugin, "id");
        this.usesKey = new NamespacedKey(plugin, "uses");
    }

    public ItemStack create(WandDefinition wand) {
        return create(wand, wand.uses());
    }

    public ItemStack create(WandDefinition wand, int uses) {
        ItemStack stack = new ItemStack(wand.material());
        int stored = wand.uses() <= 0 ? 0 : Math.max(1, uses);
        stack.editMeta(meta -> write(meta, wand, stored));
        return stack;
    }

    public List<ItemStack> bundle(WandDefinition wand, int amount) {
        int left = Math.max(0, amount);
        int max = wand.uses() > 0 ? 1 : Math.max(1, Math.min(64, wand.material().getMaxStackSize()));
        List<ItemStack> items = new ArrayList<>();
        while (left > 0) {
            int size = Math.min(max, left);
            ItemStack stack = create(wand);
            stack.setAmount(size);
            items.add(stack);
            left -= size;
        }
        return items;
    }

    public String idOf(ItemStack stack) {
        ItemMeta meta = meta(stack);
        if (meta == null) {
            return null;
        }
        String id = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (id == null || id.isBlank()) {
            return null;
        }
        return id.toLowerCase(Locale.ROOT);
    }

    public int usesOf(ItemStack stack) {
        ItemMeta meta = meta(stack);
        if (meta == null) {
            return 0;
        }
        Integer uses = meta.getPersistentDataContainer().get(usesKey, PersistentDataType.INTEGER);
        return uses == null ? 0 : uses;
    }

    public ItemStack afterUse(ItemStack stack) {
        int uses = usesOf(stack);
        if (uses <= 0) {
            return stack.clone();
        }
        int next = uses - 1;
        if (next <= 0) {
            return null;
        }
        ItemStack copy = stack.clone();
        WandDefinition wand = plugin.getPluginConfig().wand(idOf(copy));
        copy.editMeta(meta -> {
            meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, next);
            if (wand != null) {
                applyText(meta, wand, next);
            }
        });
        return copy;
    }

    private void write(ItemMeta meta, WandDefinition wand, int uses) {
        applyText(meta, wand, uses);
        meta.setUnbreakable(wand.unbreakable());
        if (wand.customModelData() > 0) {
            meta.setCustomModelData(wand.customModelData());
        }
        if (wand.glow()) {
            meta.setEnchantmentGlintOverride(true);
        }
        if (wand.uses() > 0) {
            meta.setMaxStackSize(1);
        }
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, wand.id());
        meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);
    }

    private void applyText(ItemMeta meta, WandDefinition wand, int uses) {
        if (wand.name() != null && !wand.name().isBlank()) {
            meta.displayName(TextParser.parse(fill(wand.name(), wand, uses)));
        }
        if (!wand.lore().isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : wand.lore()) {
                lore.add(TextParser.parse(fill(line, wand, uses)));
            }
            meta.lore(lore);
        }
    }

    private static String fill(String line, WandDefinition wand, int uses) {
        return line
                .replace("{uses}", uses <= 0 ? "Infinite" : Integer.toString(uses))
                .replace("{multiplier}", MoneyFormat.compact(wand.multiplier()));
    }

    private static ItemMeta meta(ItemStack stack) {
        if (stack == null || stack.getType().isAir() || !stack.hasItemMeta()) {
            return null;
        }
        return stack.getItemMeta();
    }
}
