package com.guru.reto.infrastructure.out.configuration;

import com.guru.reto.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

/**
 * Configuración de Spring para la conexión con AWS DynamoDB.
 * Crea los beans necesarios para el cliente asíncrono.
 */
@Configuration
public class DynamoDbConfig {

    @Value("${spring.properties.db_table}")
    private String table;

    @Value("${spring.properties.db_region}")
    private String region;

    /**
     * Crea el cliente base Asíncrono de DynamoDB (SDK v2).
     */
    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return  DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * Crea el cliente "Mejorado" (Enhanced Client) que permite mapear
     * POJOs (@DynamoDbBean) directamente.
     */
    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient asyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(asyncClient)
                .build();
    }

    /**
     * Crea el Bean de la tabla específica, vinculando el cliente
     * con el nombre de la tabla y el esquema de la entidad (Order.class).
     * Este es el Bean que se inyecta en el OrderAdapter.
     */
    @Bean
    public DynamoDbAsyncTable<Order> orderTable(DynamoDbEnhancedAsyncClient asyncEnhancedClient) {
        return asyncEnhancedClient.table(table, TableSchema.fromBean(Order.class));
    }
}