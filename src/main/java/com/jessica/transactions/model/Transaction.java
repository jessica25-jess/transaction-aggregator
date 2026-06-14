package com.jessica.transactions.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transactions_customer_id", columnList = "customer_id"),
        @Index(name = "idx_transactions_date", columnList = "date"),
        @Index(name = "idx_transactions_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private String id;

    @NotBlank
    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @NotBlank
    @Column(nullable = false)
    private String merchant;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionSource source;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * The raw external reference ID from the data source (e.g. Stitch transaction ID).
     * Used to prevent duplicate ingestion.
     */
    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "ZAR";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
