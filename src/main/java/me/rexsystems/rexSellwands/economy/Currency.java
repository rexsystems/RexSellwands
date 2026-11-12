package me.rexsystems.rexSellwands.economy;

import org.bukkit.entity.Player;

public interface Currency {

    Currency NONE = (player, amount) -> false;

    boolean deposit(Player player, double amount);

    default boolean withdraw(Player player, double amount) {
        return false;
    }
}
