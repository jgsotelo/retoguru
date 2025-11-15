package com.guru.reto.infrastructure.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderUpdateReq(

        @NotNull(message = "El id no debe ser null")
        @NotBlank(message = "El id no debe ser vacio")
        String id,

        @NotNull(message = "El cliente no debe ser null")
        @NotBlank(message = "El cliente no debe ser vacio")
        String customer,

        @NotNull(message = "La direccion no debe ser null")
        @NotBlank(message = "La direccion no debe ser vacio")
        String address
) {
}
