package com.guru.reto.infrastructure.util;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String field,
        String message
) {
}
