package com.guru.reto.infrastructure.in.rest.dto;

import lombok.Builder;

@Builder
public record OrderItemReq(
        String productId,
        int quantity,
        double price
) {
}