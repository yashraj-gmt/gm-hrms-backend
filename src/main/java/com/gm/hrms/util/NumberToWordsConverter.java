package com.gm.hrms.util;

import org.springframework.stereotype.Component;

/**
 * Converts a numeric amount to Indian-style words.
 * E.g. 25000.00 → "Indian Rupee Twenty-Five Thousand Only"
 */
@Component
public class NumberToWordsConverter {

    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen",
            "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    public String convert(Double amount) {
        if (amount == null || amount < 0) return "Invalid Amount";

        long rupees = amount.longValue();
        int paise   = (int) Math.round((amount - rupees) * 100);

        StringBuilder result = new StringBuilder("Indian Rupee ");
        result.append(convertToWords(rupees));

        if (paise > 0) {
            result.append(" and ").append(convertToWords(paise)).append(" Paise");
        }

        result.append(" Only");
        return result.toString();
    }

    private String convertToWords(long number) {
        if (number == 0) return "Zero";

        StringBuilder words = new StringBuilder();

        if (number >= 10_000_000) {
            words.append(convertToWords(number / 10_000_000)).append(" Crore ");
            number %= 10_000_000;
        }
        if (number >= 100_000) {
            words.append(convertToWords(number / 100_000)).append(" Lakh ");
            number %= 100_000;
        }
        if (number >= 1_000) {
            words.append(convertToWords(number / 1_000)).append(" Thousand ");
            number %= 1_000;
        }
        if (number >= 100) {
            words.append(ONES[(int)(number / 100)]).append(" Hundred ");
            number %= 100;
        }
        if (number >= 20) {
            words.append(TENS[(int)(number / 10)]).append(" ");
            number %= 10;
        }
        if (number > 0) {
            words.append(ONES[(int) number]).append(" ");
        }

        return words.toString().trim();
    }
}