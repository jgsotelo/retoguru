package com.guru.reto.infrastructure.in.rest.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.util.List;

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
