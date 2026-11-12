package me.rexsystems.rexSellwands.sell;

import me.rexsystems.rexSellwands.RexSellwands;
import me.rexsystems.rexSellwands.config.WandDefinition;
import me.rexsystems.rexSellwands.util.SchedulerUtils;
import me.rexsystems.rexSellwands.util.TextParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class Feedback {

    private final RexSellwands plugin;
    private final Set<UUID> holograms = ConcurrentHashMap.newKeySet();
    private final Set<String> unknownSounds = ConcurrentHashMap.newKeySet();
    private final Set<String> unknownParticles = ConcurrentHashMap.newKeySet();

    public Feedback(RexSellwands plugin) {
        this.plugin = plugin;
    }

    public void success(Player player, Location block, WandDefinition wand, Map<String, String> tokens) {
        if (block == null || block.getWorld() == null) {
            return;
        }
        Location at = block.clone().add(0.5, 0.8, 0.5);
        playSound(player, at, wand.sound());
        spawnParticles(at, wand.particles());
        spawnHologram(at, wand.hologram(), tokens);
    }

    public void clear() {
        for (UUID id : Set.copyOf(holograms)) {
            try {
                Entity entity = plugin.getServer().getEntity(id);
                if (entity != null && entity.isValid()) {
                    entity.remove();
                }
            } catch (Throwable ignored) {
            }
        }
        holograms.clear();
    }

    private void playSound(Player player, Location at, String name) {
        if (player == null || name == null || name.isBlank()) {
            return;
        }
        String key = name.trim().toUpperCase(Locale.ROOT);
        Sound sound;
        try {
            sound = Sound.valueOf(key);
        } catch (IllegalArgumentException ex) {
            if (unknownSounds.add(key)) {
                plugin.getLogger().warning("Unknown sound " + name + ".");
            }
            return;
        }
        player.playSound(at, sound, 1f, 1f);
    }

    private void spawnParticles(Location at, String name) {
        if (name == null || name.isBlank() || at.getWorld() == null) {
            return;
        }
        String key = name.trim().toUpperCase(Locale.ROOT).replace("VILLAGER_HAPPY", "HAPPY_VILLAGER");
        Particle particle;
        try {
            particle = Particle.valueOf(key);
        } catch (IllegalArgumentException ex) {
            if (unknownParticles.add(key)) {
                plugin.getLogger().warning("Unknown particle " + name + ".");
            }
            return;
        }
        try {
            at.getWorld().spawnParticle(particle, at, 12, 0.25, 0.25, 0.25, 0);
        } catch (IllegalArgumentException ex) {
            if (unknownParticles.add(key)) {
                plugin.getLogger().warning("Particle " + name + " needs extra data and was skipped.");
            }
        }
    }

    private void spawnHologram(Location at, String template, Map<String, String> tokens) {
        double seconds = plugin.getPluginConfig().hologramSeconds();
        if (seconds <= 0 || template == null || template.isBlank() || at.getWorld() == null) {
            return;
        }
        Component text = TextParser.parse(TextParser.apply(template, tokens));
        if (PlainTextComponentSerializer.plainText().serialize(text).isBlank()) {
            return;
        }
        Location raised = at.clone().add(0, 0.45, 0);
        TextDisplay display = raised.getWorld().spawn(raised, TextDisplay.class, entity -> {
            entity.text(text);
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setSeeThrough(true);
            entity.setShadowed(true);
            entity.setDefaultBackground(false);
            entity.setAlignment(TextDisplay.TextAlignment.CENTER);
            entity.setShadowRadius(0f);
            entity.setShadowStrength(0f);
            entity.setPersistent(false);
            entity.setInvulnerable(true);
        });
        holograms.add(display.getUniqueId());
        long ticks = Math.max(1L, Math.round(seconds * 20.0d));
        SchedulerUtils.runAtLocationLater(plugin, raised, () -> {
            holograms.remove(display.getUniqueId());
            if (display.isValid()) {
                display.remove();
            }
        }, ticks);
    }
}
