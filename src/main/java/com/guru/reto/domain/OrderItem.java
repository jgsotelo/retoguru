package com.guru.reto.domain;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 4648929576102574063L;

    private String productId;
    private int quantity;
    private double price;
}