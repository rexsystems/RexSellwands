package me.rexsystems.rexSellwands.sell;

import java.util.List;

public record SaleQuote(double payout, List<ItemLine> remaining, List<String> unsellable, boolean soldAnything) {

    public SaleQuote {
        remaining = remaining == null ? List.of() : List.copyOf(remaining);
        unsellable = unsellable == null ? List.of() : List.copyOf(unsellable);
        if (!Double.isFinite(payout) || payout < 0) {
            payout = 0;
        }
    }
}
