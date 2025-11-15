package com.guru.reto.infrastructure.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guru.reto.domain.Order;
import lombok.Builder;

import java.time.Instant;

/**
 * DTO (Data Transfer Object) para la respuesta (Response Body)
 * que se envía al cliente.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderResponse(
        String id,
        String creation,
        String status
) {

    /**
     * Factory Method para mapear la entidad de Dominio a este DTO de respuesta.
     * @param order La entidad Order del dominio.
     * @return El DTO de respuesta.
     */
    public static OrderResponse from(final Order order) {
        return OrderResponse.builder()
                .id(order.getOrderId())
                .creation(Instant.now().toString())
                .status(order.getStatus())
                .build();
    }
}
