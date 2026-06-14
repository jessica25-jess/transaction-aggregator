-- V1__create_transactions_table.sql
-- Initial schema for the transaction aggregator

CREATE TABLE IF NOT EXISTS transactions (
    id           VARCHAR(36)    NOT NULL,
    customer_id  VARCHAR(255)   NOT NULL,
    merchant     VARCHAR(255)   NOT NULL,
    amount       NUMERIC(19, 4) NOT NULL,
    currency     VARCHAR(3)     NOT NULL DEFAULT 'ZAR',
    category     VARCHAR(50)    NOT NULL,
    source       VARCHAR(50)    NOT NULL,
    date         TIMESTAMP      NOT NULL,
    external_id  VARCHAR(255)   UNIQUE,
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT chk_amount_positive CHECK (amount > 0)
);

CREATE INDEX idx_transactions_customer_id ON transactions (customer_id);
CREATE INDEX idx_transactions_date        ON transactions (date);
CREATE INDEX idx_transactions_category    ON transactions (category);
CREATE INDEX idx_transactions_customer_date
    ON transactions (customer_id, date DESC);
