package com.guru.reto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order implements Serializable {

    private String orderId;
    private String customerId;
    private Instant orderDate;
    private List<OrderItem> items;
    private String status;
    private Long version;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }
}