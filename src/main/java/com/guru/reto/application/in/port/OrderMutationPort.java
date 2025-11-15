package com.guru.reto.application.in.port;

import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderResponse;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import reactor.core.publisher.Mono;

/**
 * Puerto de Entrada (Inbound Port) que define los casos de uso
 * para la mutación (escritura) de Órdenes.
 * La capa de infraestructura (ej. Handlers) llama a este puerto.
 */
public interface OrderMutationPort {

    /**
     * Caso de uso: Registrar una nueva orden.
     * @param req DTO de registro.
     * @return DTO de respuesta.
     */
    Mono<OrderResponse> registerOrder(OrderRegisterReq req);

    /**
     * Caso de uso: Actualizar una orden existente.
     * @param req DTO de actualización.
     * @return DTO de respuesta.
     */
    Mono<OrderResponse> updateOrder(OrderUpdateReq req);
}
