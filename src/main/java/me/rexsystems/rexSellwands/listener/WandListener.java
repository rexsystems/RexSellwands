package me.rexsystems.rexSellwands.listener;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.WandDefinition;
import me.rexsystems.rexSellwands.sell.Containers;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class WandListener implements Listener {

    private final RexSellwands plugin;

    public WandListener(RexSellwands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        EquipmentSlot hand = event.getHand();
        if (block == null || item == null || hand == null) {
            return;
        }
        String wandId = plugin.getWandItems().idOf(item);
        WandDefinition wand = wandId == null ? null : plugin.getPluginConfig().wand(wandId);
        if (wand == null || !wand.allows(block.getType())) {
            return;
        }

        // Only this hand, and only when the block is one of the wand's containers.
        event.setCancelled(true);

        Player player = event.getPlayer();
        if (!player.hasPermission("rexsellwands.use")) {
            plugin.getPluginConfig().send(player, "no-permission", Map.of());
            return;
        }
        Inventory inventory = Containers.of(player, block);
        if (inventory == null) {
            return;
        }
        if (action == Action.LEFT_CLICK_BLOCK) {
            plugin.getSales().preview(player, wand, inventory);
            return;
        }
        plugin.getSales().sell(player, wand, item, hand, block, inventory);
    }
}
