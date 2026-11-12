package me.rexsystems.rexSellwands.sell;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SaleMathTest {

    @Test
    void stackPriceIncludesAmountAndMultiplier() {
        ItemLine stack = new ItemLine(0, "DIAMOND", 64, List.of());
        PriceBook book = new PriceBook(Map.of("DIAMOND", 10.0), Set.of());

        SaleQuote quote = SaleMath.quote(List.of(stack), book, false, 1.5);

        assertEquals(960.0, quote.payout(), 0.000001);
        assertTrue(quote.soldAnything());
        assertTrue(quote.remaining().isEmpty());
    }

    @Test
    void blacklistIsSkipped() {
        ItemLine diamonds = new ItemLine(0, "DIAMOND", 2, List.of());
        ItemLine bedrock = new ItemLine(1, "BEDROCK", 3, List.of());
        PriceBook book = new PriceBook(Map.of("DIAMOND", 10.0, "BEDROCK", 999.0), Set.of("BEDROCK"));

        SaleQuote quote = SaleMath.quote(List.of(diamonds, bedrock), book, false, 1.0);

        assertEquals(20.0, quote.payout(), 0.000001);
        assertEquals(1, quote.remaining().size());
        assertEquals("BEDROCK", quote.remaining().getFirst().material());
        assertEquals(3, quote.remaining().getFirst().amount());
        assertFalse(quote.unsellable().contains("BEDROCK"));
    }

    @Test
    void shulkerSellsAsOneItemWhenContentsFlagIsOff() {
        ItemLine diamonds = new ItemLine(0, "DIAMOND", 64, List.of());
        ItemLine box = new ItemLine(0, "SHULKER_BOX", 1, List.of(diamonds));
        PriceBook book = new PriceBook(Map.of("SHULKER_BOX", 50.0, "DIAMOND", 100.0), Set.of());

        SaleQuote quote = SaleMath.quote(List.of(box), book, false, 2.0);

        assertEquals(100.0, quote.payout(), 0.000001);
        assertTrue(quote.remaining().isEmpty());
        assertFalse(quote.unsellable().contains("DIAMOND"));
    }

    @Test
    void failedDepositRestoresItems() {
        List<ItemLine> snapshot = List.of(
                new ItemLine(0, "DIAMOND", 10, List.of()),
                new ItemLine(1, "DIRT", 5, List.of())
        );
        PriceBook book = new PriceBook(Map.of("DIAMOND", 4.0), Set.of());
        SaleQuote quote = SaleMath.quote(snapshot, book, false, 1.0);

        assertEquals(40.0, quote.payout(), 0.000001);
        assertTrue(quote.unsellable().contains("DIRT"));
        assertNotEquals(snapshot, quote.remaining());

        Settlement failed = SaleSettlement.settle(snapshot, quote, false);
        assertTrue(failed.restored());
        assertFalse(failed.paid());
        assertFalse(failed.decrementUses());
        assertEquals(0.0, failed.payout(), 0.000001);
        assertEquals(snapshot, failed.contents());

        Settlement paid = SaleSettlement.settle(snapshot, quote, true);
        assertFalse(paid.restored());
        assertTrue(paid.paid());
        assertTrue(paid.decrementUses());
        assertEquals(quote.remaining(), paid.contents());
        assertEquals("DIRT", paid.contents().getFirst().material());
    }
}
