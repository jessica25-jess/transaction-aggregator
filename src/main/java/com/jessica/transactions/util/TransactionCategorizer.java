package com.jessica.transactions.util;

import com.jessica.transactions.model.Category;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Categorises a transaction based on merchant name using keyword matching.
 * Keywords are checked in insertion order; the first match wins.
 * To extend categorisation, add entries to KEYWORD_CATEGORY_MAP.
 */
@Component
public class TransactionCategorizer {

    private static final Map<String, Category> KEYWORD_CATEGORY_MAP = new LinkedHashMap<>();

    static {
        // Transport
        KEYWORD_CATEGORY_MAP.put("uber", Category.TRANSPORT);
        KEYWORD_CATEGORY_MAP.put("bolt", Category.TRANSPORT);
        KEYWORD_CATEGORY_MAP.put("gautrain", Category.TRANSPORT);
        KEYWORD_CATEGORY_MAP.put("metrobus", Category.TRANSPORT);
        KEYWORD_CATEGORY_MAP.put("intercape", Category.TRANSPORT);

        // Groceries
        KEYWORD_CATEGORY_MAP.put("checkers", Category.GROCERIES);
        KEYWORD_CATEGORY_MAP.put("woolworths", Category.GROCERIES);
        KEYWORD_CATEGORY_MAP.put("pick n pay", Category.GROCERIES);
        KEYWORD_CATEGORY_MAP.put("spar", Category.GROCERIES);
        KEYWORD_CATEGORY_MAP.put("shoprite", Category.GROCERIES);
        KEYWORD_CATEGORY_MAP.put("food lover", Category.GROCERIES);

        // Dining
        KEYWORD_CATEGORY_MAP.put("restaurant", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("cafe", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("mcdonald", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("kfc", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("nando", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("steers", Category.DINING);
        KEYWORD_CATEGORY_MAP.put("debonairs", Category.DINING);

        // Entertainment
        KEYWORD_CATEGORY_MAP.put("netflix", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("showmax", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("dstv", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("apple music", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("spotify", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("steam", Category.ENTERTAINMENT);
        KEYWORD_CATEGORY_MAP.put("playstation", Category.ENTERTAINMENT);

        // Utilities
        KEYWORD_CATEGORY_MAP.put("eskom", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("city power", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("telkom", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("vodacom", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("mtn", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("cell c", Category.UTILITIES);
        KEYWORD_CATEGORY_MAP.put("rain", Category.UTILITIES);

        // Healthcare
        KEYWORD_CATEGORY_MAP.put("pharmacy", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("clicks", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("dischem", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("hospital", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("medical", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("discovery", Category.HEALTHCARE);
        KEYWORD_CATEGORY_MAP.put("mediclinic", Category.HEALTHCARE);

        // Shopping
        KEYWORD_CATEGORY_MAP.put("takealot", Category.SHOPPING);
        KEYWORD_CATEGORY_MAP.put("amazon", Category.SHOPPING);
        KEYWORD_CATEGORY_MAP.put("mr price", Category.SHOPPING);
        KEYWORD_CATEGORY_MAP.put("h&m", Category.SHOPPING);
        KEYWORD_CATEGORY_MAP.put("zara", Category.SHOPPING);
        KEYWORD_CATEGORY_MAP.put("sportscene", Category.SHOPPING);

        // Income
        KEYWORD_CATEGORY_MAP.put("salary", Category.INCOME);
        KEYWORD_CATEGORY_MAP.put("payroll", Category.INCOME);
        KEYWORD_CATEGORY_MAP.put("wages", Category.INCOME);
        KEYWORD_CATEGORY_MAP.put("dividend", Category.INCOME);

        // Transfers
        KEYWORD_CATEGORY_MAP.put("transfer", Category.TRANSFERS);
        KEYWORD_CATEGORY_MAP.put("payment to", Category.TRANSFERS);
        KEYWORD_CATEGORY_MAP.put("eft", Category.TRANSFERS);
    }

    /**
     * Categorises a merchant name.
     *
     * @param merchant the raw merchant string (may be null or blank)
     * @return the best-matching {@link Category}, or {@link Category#OTHER} if no match found
     */
    public Category categorize(String merchant) {
        if (merchant == null || merchant.isBlank()) {
            return Category.OTHER;
        }

        String normalised = merchant.toLowerCase().trim();

        return KEYWORD_CATEGORY_MAP.entrySet().stream()
                .filter(entry -> normalised.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Category.OTHER);
    }
}
