package com.jessica.transactions.repository;

import com.jessica.transactions.model.Category;
import com.jessica.transactions.model.Transaction;
import com.jessica.transactions.model.TransactionSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findByCustomerId(String customerId, Pageable pageable);

    Page<Transaction> findByCustomerIdAndCategory(String customerId, Category category, Pageable pageable);

    Page<Transaction> findByCustomerIdAndDateBetween(
            String customerId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Transaction> findByCustomerIdAndCategoryAndDateBetween(
            String customerId, Category category, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Optional<Transaction> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);

    /**
     * Returns total spend per category for a given customer, pushed entirely to the DB.
     */
    @Query("""
            SELECT t.category AS category, SUM(t.amount) AS total
            FROM Transaction t
            WHERE t.customerId = :customerId
            GROUP BY t.category
            ORDER BY total DESC
            """)
    List<CategoryTotal> sumAmountByCategory(@Param("customerId") String customerId);

    /**
     * Returns total spend per category within a date range.
     */
    @Query("""
            SELECT t.category AS category, SUM(t.amount) AS total
            FROM Transaction t
            WHERE t.customerId = :customerId
              AND t.date BETWEEN :from AND :to
            GROUP BY t.category
            ORDER BY total DESC
            """)
    List<CategoryTotal> sumAmountByCategoryAndDateRange(
            @Param("customerId") String customerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    /**
     * Top merchants by total spend for a customer.
     */
    @Query("""
            SELECT t.merchant AS merchant, SUM(t.amount) AS total, COUNT(t) AS count
            FROM Transaction t
            WHERE t.customerId = :customerId
            GROUP BY t.merchant
            ORDER BY total DESC
            """)
    List<MerchantTotal> topMerchantsBySpend(@Param("customerId") String customerId, Pageable pageable);

    /**
     * Monthly spend totals for trend analysis.
     */
    @Query(value = """
            SELECT DATE_TRUNC('month', date) AS month, SUM(amount) AS total
            FROM transactions
            WHERE customer_id = :customerId
            GROUP BY month
            ORDER BY month ASC
            """, nativeQuery = true)
    List<MonthlyTotal> monthlySpendTotals(@Param("customerId") String customerId);

    @Query("""
            SELECT SUM(t.amount) FROM Transaction t
            WHERE t.customerId = :customerId
              AND t.date BETWEEN :from AND :to
            """)
    Optional<BigDecimal> totalSpendInPeriod(
            @Param("customerId") String customerId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

    long countByCustomerIdAndSource(String customerId, TransactionSource source);

    // ── Projections ──────────────────────────────────────────────────────────

    interface CategoryTotal {
        Category getCategory();
        BigDecimal getTotal();
    }

    interface MerchantTotal {
        String getMerchant();
        BigDecimal getTotal();
        Long getCount();
    }

    interface MonthlyTotal {
        java.sql.Timestamp getMonth();
        BigDecimal getTotal();
    }
}
