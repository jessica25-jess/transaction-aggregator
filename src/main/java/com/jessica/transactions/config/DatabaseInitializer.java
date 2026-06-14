package com.jessica.transactions.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Automatically creates the application database on startup if it does not
 * already exist. This runs before Flyway, so the schema migration always has
 * a database to connect to.
 *
 * How it works:
 *  1. Connects to the PostgreSQL server using the built-in "postgres" maintenance DB.
 *  2. Checks whether "transactions_db" already exists.
 *  3. Creates it if not — then disconnects.
 *  4. Flyway then takes over and applies the schema migrations as normal.
 *
 * This means you only need PostgreSQL installed and running — no manual
 * "CREATE DATABASE" step required.
 */
@Slf4j
@Configuration
public class DatabaseInitializer {

    @Value("${app.datasource.host:localhost}")
    private String host;

    @Value("${app.datasource.port:5432}")
    private String port;

    @Value("${app.datasource.dbname:transactions_db}")
    private String dbName;

    @Value("${spring.datasource.username:postgres}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * Wraps Flyway's migration strategy so we can ensure the database exists
     * before Flyway tries to connect to it.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            ensureDatabaseExists();
            flyway.migrate();
        };
    }

    private void ensureDatabaseExists() {
        // Connect to the "postgres" maintenance database — it always exists
        String maintenanceUrl = String.format(
                "jdbc:postgresql://%s:%s/postgres", host, port);

        try (Connection conn = DriverManager.getConnection(maintenanceUrl, username, password);
             Statement stmt = conn.createStatement()) {

            // Check if the target database already exists
            ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'");

            if (!rs.next()) {
                log.info("Database '{}' not found — creating it now...", dbName);
                // CREATE DATABASE cannot run inside a transaction, so we need autocommit
                conn.setAutoCommit(true);
                stmt.executeUpdate("CREATE DATABASE \"" + dbName + "\"");
                log.info("Database '{}' created successfully.", dbName);
            } else {
                log.info("Database '{}' already exists — skipping creation.", dbName);
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to auto-create database '" + dbName + "'. " +
                    "Please check your PostgreSQL credentials and that the server is running. " +
                    "Error: " + e.getMessage(), e);
        }
    }
}
