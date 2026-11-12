package me.rexsystems.rexSellwands.sell;

import java.util.List;

public final class SaleSettlement {

    private SaleSettlement() {
    }

    public static Settlement settle(List<ItemLine> snapshot, SaleQuote quote, boolean depositSucceeds) {
        List<ItemLine> original = snapshot == null ? List.of() : snapshot;
        if (quote == null || !quote.soldAnything()) {
            return new Settlement(original, 0, false, false, false);
        }
        if (!depositSucceeds) {
            return new Settlement(original, 0, false, true, false);
        }
        return new Settlement(quote.remaining(), quote.payout(), true, false, true);
    }
}
