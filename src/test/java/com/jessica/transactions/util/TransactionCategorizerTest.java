package com.jessica.transactions.util;

import com.jessica.transactions.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TransactionCategorizer")
class TransactionCategorizerTest {

    private TransactionCategorizer categorizer;

    @BeforeEach
    void setUp() {
        categorizer = new TransactionCategorizer();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("null and blank merchant returns OTHER")
    void blankOrNullMerchant_returnsOther(String merchant) {
        assertThat(categorizer.categorize(merchant)).isEqualTo(Category.OTHER);
    }

    @ParameterizedTest
    @CsvSource({
            "Uber, TRANSPORT",
            "UBER* EATS, TRANSPORT",
            "uber trip, TRANSPORT",
            "Bolt Ride, TRANSPORT",
            "BOLT TECHNOLOGIES, TRANSPORT"
    })
    @DisplayName("transport merchants are categorised correctly")
    void transportMerchants(String merchant, Category expected) {
        assertThat(categorizer.categorize(merchant)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "Checkers Hyper, GROCERIES",
            "CHECKERS HYP PRETORIA, GROCERIES",
            "Woolworths Food, GROCERIES",
            "WW FOOD, OTHER",
            "Pick n Pay Hatfield, GROCERIES",
            "SPAR EXPRESS, GROCERIES"
    })
    @DisplayName("grocery merchants are categorised correctly")
    void groceryMerchants(String merchant, Category expected) {
        assertThat(categorizer.categorize(merchant)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "Netflix.com, ENTERTAINMENT",
            "NETFLIX SUBSCRIPTION, ENTERTAINMENT",
            "Showmax, ENTERTAINMENT",
            "Spotify Premium, ENTERTAINMENT",
            "DSTV Subscription, ENTERTAINMENT"
    })
    @DisplayName("entertainment merchants are categorised correctly")
    void entertainmentMerchants(String merchant, Category expected) {
        assertThat(categorizer.categorize(merchant)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "Salary Payment - Employer, INCOME",
            "SALARY CREDIT, INCOME",
            "Payroll ZA, INCOME",
            "Dividend Payment, INCOME"
    })
    @DisplayName("income transactions are categorised correctly")
    void incomeTransactions(String merchant, Category expected) {
        assertThat(categorizer.categorize(merchant)).isEqualTo(expected);
    }

    @Test
    @DisplayName("completely unknown merchant returns OTHER")
    void unknownMerchant_returnsOther() {
        assertThat(categorizer.categorize("Some Random Shop XYZ")).isEqualTo(Category.OTHER);
    }

    @Test
    @DisplayName("categorisation is case-insensitive")
    void caseInsensitive() {
        assertThat(categorizer.categorize("WOOLWORTHS")).isEqualTo(Category.GROCERIES);
        assertThat(categorizer.categorize("woolworths")).isEqualTo(Category.GROCERIES);
        assertThat(categorizer.categorize("Woolworths")).isEqualTo(Category.GROCERIES);
    }

    @Test
    @DisplayName("first matching keyword wins")
    void firstMatchWins() {
        // "transfer" is mapped to TRANSFERS; if a merchant name also contains
        // another keyword, the first match in insertion order should win
        assertThat(categorizer.categorize("EFT Transfer")).isEqualTo(Category.TRANSFERS);
    }
}
