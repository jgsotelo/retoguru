package com.guru.reto.infrastructure.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guru.reto.domain.Order;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        String id,
        String creation,
        String status
) {

    public static OrderResponse from(final Order order) {
        return OrderResponse.builder()
                .id(order.getOrderId())
                .creation(order.getOrderDate().toString())
                .status(order.getStatus())
                .build();
    }
}
