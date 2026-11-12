package me.rexsystems.rexSellwands.sell;

import java.util.List;

public record Settlement(
        List<ItemLine> contents,
        double payout,
        boolean paid,
        boolean restored,
        boolean decrementUses
) {

    public Settlement {
        contents = contents == null ? List.of() : List.copyOf(contents);
        if (!Double.isFinite(payout) || payout < 0) {
            payout = 0;
        }
    }
}
