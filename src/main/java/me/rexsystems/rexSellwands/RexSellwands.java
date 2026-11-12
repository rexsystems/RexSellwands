package me.rexsystems.rexSellwands;

import me.rexsystems.rexSellwands.command.SellWandsCommand;
import me.rexsystems.rexSellwands.config.PluginConfig;
import me.rexsystems.rexSellwands.listener.WandListener;
import me.rexsystems.rexSellwands.sell.Feedback;
import me.rexsystems.rexSellwands.sell.SaleService;
import me.rexsystems.rexSellwands.wand.CooldownBook;
import me.rexsystems.rexSellwands.wand.WandItems;
import org.bukkit.command.PluginCommand;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class RexSellwands extends JavaPlugin {

    private static final int BSTATS_PLUGIN_ID = 35267;

    private PluginConfig pluginConfig;
    private CooldownBook cooldowns;
    private WandItems wandItems;
    private SaleService sales;
    private Feedback feedback;

    @Override
    public void onEnable() {
        this.cooldowns = new CooldownBook();
        this.pluginConfig = new PluginConfig(this);
        this.pluginConfig.load();
        this.wandItems = new WandItems(this);
        this.feedback = new Feedback(this);
        this.sales = new SaleService(this);

        getServer().getPluginManager().registerEvents(new WandListener(this), this);

        SellWandsCommand command = new SellWandsCommand(this);
        PluginCommand pluginCommand = getCommand("rexsellwands");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        new Metrics(this, BSTATS_PLUGIN_ID);
        getLogger().info("RexSellwands enabled.");
    }

    @Override
    public void onDisable() {
        if (feedback != null) {
            feedback.clear();
        }
        getLogger().info("RexSellwands disabled.");
    }

    public void reloadPlugin() {
        pluginConfig.load();
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public CooldownBook getCooldowns() {
        return cooldowns;
    }

    public WandItems getWandItems() {
        return wandItems;
    }

    public SaleService getSales() {
        return sales;
    }

    public Feedback getFeedback() {
        return feedback;
    }
}
