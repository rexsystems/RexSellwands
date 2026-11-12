package me.rexsystems.rexSellwands.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MoneyFormat {

    private MoneyFormat() {
    }

    public static String format(double amount, String pattern) {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        String used = pattern == null || pattern.isBlank() ? "#,##0.##" : pattern;
        DecimalFormat format;
        try {
            format = new DecimalFormat(used, symbols);
        } catch (IllegalArgumentException ex) {
            format = new DecimalFormat("#,##0.##", symbols);
        }
        return format.format(amount);
    }

    public static String commandAmount(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static String compact(double amount) {
        if (!Double.isFinite(amount)) {
            return "0";
        }
        return BigDecimal.valueOf(amount)
                .setScale(4, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public static Double parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String cleaned = raw.replaceAll("<[^>]+>", "").replaceAll("§.", "");
        cleaned = cleaned.replaceAll("[^0-9,.-]", "");
        if (cleaned.isBlank() || cleaned.equals("-") || cleaned.equals(".")) {
            return null;
        }
        if (cleaned.indexOf(',') >= 0 && cleaned.indexOf('.') >= 0) {
            cleaned = cleaned.replace(",", "");
        } else if (cleaned.indexOf(',') >= 0) {
            int last = cleaned.lastIndexOf(',');
            if (cleaned.length() - last <= 3) {
                cleaned = cleaned.replace(',', '.');
            } else {
                cleaned = cleaned.replace(",", "");
            }
        }
        try {
            double value = Double.parseDouble(cleaned);
            return Double.isFinite(value) ? value : null;
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
