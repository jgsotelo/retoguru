package com.guru.reto.domain;

import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import com.guru.reto.infrastructure.util.Constants;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = -8727188434162167259L;

    private String orderId;
    private Long version;

    @Getter
    private String customerId;

    @Getter
    private String address;

    @Getter
    private Instant orderDate;

    @Getter
    private Instant orderUpdate;

    @Getter
    private List<OrderItem> items;

    @Getter
    private String status;

    @DynamoDbPartitionKey
    public String getOrderId() {
        return orderId;
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }

    public static Order fromRegister(final OrderRegisterReq req) {
        return Order.builder()
                .orderId(Constants.generateId())
                .customerId(req.customer())
                .address(req.address())
                .items(req.items().stream()
                        .map(item -> OrderItem.builder()
                                .productId(item.productId())
                                .quantity(item.quantity())
                                .price(item.price())
                                .build())
                        .toList())
                .orderDate(Instant.now())
                .status(Constants.STATUS_PENDING)
                .build();
    }

    public static Order fromUpdate(final OrderUpdateReq req) {
        return Order.builder()
                .orderId(req.id())
                .customerId(req.customer())
                .address(req.address())
                .build();
    }
}