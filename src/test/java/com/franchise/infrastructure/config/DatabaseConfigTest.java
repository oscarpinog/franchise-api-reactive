package com.franchise.infrastructure.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseConfigTest {

    @Test
    @DisplayName("Debe crear el ConnectionFactoryInitializer correctamente")
    void shouldCreateInitializer() {
        // GIVEN
        DatabaseConfig databaseConfig = new DatabaseConfig();
        ConnectionFactory connectionFactory = Mockito.mock(ConnectionFactory.class);

        // WHEN
        ConnectionFactoryInitializer initializer = databaseConfig.initializer(connectionFactory);

        // THEN
        assertThat(initializer).isNotNull();
        // Verificamos que sea la clase correcta
        assertThat(initializer).isInstanceOf(ConnectionFactoryInitializer.class);
    }
}