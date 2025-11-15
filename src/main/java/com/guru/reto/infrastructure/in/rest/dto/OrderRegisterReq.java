package com.guru.reto.infrastructure.in.rest.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * DTO (Data Transfer Object) que representa la solicitud de entrada (Request Body)
 * para registrar una nueva orden (POST /orders).
 * * Es un 'record' de Java para inmutabilidad y concisión.
 * Las anotaciones jakarta.validation son detectadas por WebFluxConfig.
 */
@Builder
public record OrderRegisterReq(

        @NotNull(message = "El cliente no debe ser null")
        @NotBlank(message = "El cliente no debe ser vacio")
        String customer,

        @NotNull(message = "La direccion no debe ser null")
        @NotBlank(message = "La direccion no debe ser vacio")
        String address,

        @NotNull(message = "El detalle no debe ser null")
        @NotEmpty(message = "El detalle no debe ser vacio")
        List<OrderItemReq> items
) {
}
