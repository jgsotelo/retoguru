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

@Configuration
public class DynamoDbConfig {

    @Value("${spring.properties.db_table}")
    private String table;

    @Value("${spring.properties.db_region}")
    private String region;


    @Bean
    public DynamoDbAsyncClient dynamoDbAsyncClient() {
        return  DynamoDbAsyncClient.builder()
                .region(Region.of(region))
                .build();
    }

    @Bean
    public DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient(DynamoDbAsyncClient asyncClient) {
        return DynamoDbEnhancedAsyncClient.builder()
                .dynamoDbClient(asyncClient)
                .build();
    }

    @Bean
    public DynamoDbAsyncTable<Order> orderTable(DynamoDbEnhancedAsyncClient asyncEnhancedClient) {
        return asyncEnhancedClient.table(table, TableSchema.fromBean(Order.class));
    }
}