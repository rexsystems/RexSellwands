package me.rexsystems.rexSellwands.util;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class Audience {

    private Audience() {
    }

    public static void send(CommandSender sender, String template, Map<String, String> tokens) {
        if (sender == null || template == null || template.isBlank()) {
            return;
        }
        String text = TextParser.apply(template, tokens);
        if (sender instanceof Player player) {
            text = Placeholders.apply(player, text);
        }
        MessageService.send(sender, text);
    }
}
