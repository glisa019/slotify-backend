package com.myslotify.slotify.configuration;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.spi.Stoppable;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * MultiTenantConnectionProvider implementation for schema-based multi-tenancy.
 */
@Component
public class SchemaBasedMultiTenantConnectionProvider implements MultiTenantConnectionProvider, Stoppable {

    private final DataSource dataSource;

    public SchemaBasedMultiTenantConnectionProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Get a connection for a specific tenant/schema.
     *
     * @param tenantIdentifier the schema name (tenant identifier) as an Object
     * @return the database connection set to the specified schema
     * @throws SQLException if an error occurs while setting the schema
     */
    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        if (tenantIdentifier == null) {
            throw new IllegalArgumentException("Tenant identifier cannot be null");
        }
        String schema = tenantIdentifier.toString(); // Convert tenantIdentifier to String
        Connection connection = dataSource.getConnection();
        connection.setSchema(schema); // Set schema dynamically
        return connection;
    }

    /**
     * Release a connection for a specific tenant/schema.
     *
     * @param tenantIdentifier the schema name (tenant identifier) as an Object
     * @param connection       the database connection to release
     * @throws SQLException if an error occurs while closing the connection
     */
    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Get a connection without a specific tenant/schema.
     *
     * @return a database connection
     * @throws SQLException if an error occurs while getting the connection
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Release a connection without a specific tenant/schema.
     *
     * @param connection the database connection to release
     * @throws SQLException if an error occurs while closing the connection
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        if (connection != null) {
            connection.close();
        }
    } 

    /**
     * Check if the underlying database supports multi-tenancy.
     *
     * @return true, indicating support for multi-tenancy
     */
    @Override
    public boolean supportsAggressiveRelease() {
        return false; // Most connection pools prefer retaining connections
    }

    /**
     * Check if the provider supports the specified class of JDBC connection.
     *
     * @param unwrapType the connection class to check
     * @return true if the class is supported, otherwise false
     */
    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return unwrapType.isAssignableFrom(DataSource.class);
    }

    /**
     * Unwrap the provider to the specified class.
     *
     * @param unwrapType the class to unwrap to
     * @return the unwrapped provider
     */
    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) dataSource;
        }
        return null;
    }

    /**
     * Called when the provider is no longer needed.
     */
    @Override
    public void stop() {
        // No specific cleanup required in this implementation
    }
}
