package me.rexsystems.rexSellwands.sell;

import java.util.ArrayList;
import java.util.List;

public final class SaleMath {

    private SaleMath() {
    }

    public static SaleQuote quote(List<ItemLine> contents, PriceBook book, boolean sellShulkerContents, double multiplier) {
        List<ItemLine> source = contents == null ? List.of() : contents;
        if (book == null || !Double.isFinite(multiplier) || multiplier <= 0) {
            return new SaleQuote(0, source, List.of(), false);
        }
        List<ItemLine> remaining = new ArrayList<>();
        List<String> unsellable = new ArrayList<>();
        double payout = 0;
        boolean sold = false;
        for (ItemLine line : source) {
            Outcome outcome = priceLine(line, book, sellShulkerContents, multiplier, unsellable);
            payout += outcome.payout;
            sold = sold || outcome.sold;
            if (outcome.keep != null) {
                remaining.add(outcome.keep);
            }
        }
        return new SaleQuote(payout, remaining, unsellable, sold);
    }

    private static Outcome priceLine(
            ItemLine line,
            PriceBook book,
            boolean sellShulkerContents,
            double multiplier,
            List<String> unsellable
    ) {
        if (line == null || line.amount() <= 0 || line.material().isBlank()) {
            return Outcome.skip();
        }
        if (book.blacklisted(line.material())) {
            return Outcome.keep(line);
        }
        if (line.shulker() && sellShulkerContents) {
            // The box stays. Only the stacks inside it can be sold.
            SaleQuote inner = quote(line.contents(), book, true, multiplier);
            unsellable.addAll(inner.unsellable());
            return new Outcome(inner.payout(), inner.soldAnything(), line.withContents(inner.remaining()));
        }
        Double price = book.price(line.material());
        if (price == null) {
            unsellable.add(line.material());
            return Outcome.keep(line);
        }
        double total = price * line.amount() * multiplier;
        if (!Double.isFinite(total) || total <= 0) {
            unsellable.add(line.material());
            return Outcome.keep(line);
        }
        return Outcome.sell(total);
    }

    private record Outcome(double payout, boolean sold, ItemLine keep) {

        private static Outcome skip() {
            return new Outcome(0, false, null);
        }

        private static Outcome keep(ItemLine line) {
            return new Outcome(0, false, line);
        }

        private static Outcome sell(double payout) {
            return new Outcome(payout, true, null);
        }
    }
}
