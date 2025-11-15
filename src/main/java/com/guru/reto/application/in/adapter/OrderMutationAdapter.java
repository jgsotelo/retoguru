package com.guru.reto.application.in.adapter;

import com.guru.reto.application.in.port.OrderMutationPort;
import com.guru.reto.application.out.port.OrderPort;
import com.guru.reto.domain.Order;
import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderResponse;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Adaptador de Aplicación (Implementación del Puerto de Entrada).
 * Orquesta el flujo de datos para los casos de uso de mutación.
 */
@Service
@AllArgsConstructor
public class OrderMutationAdapter implements OrderMutationPort {

    private final OrderPort orderPort;

    /**
     * Orquesta el registro de una orden:
     * 1. Recibe el DTO.
     * 2. Mapea DTO -> Dominio (Order::fromRegister).
     * 3. Llama al puerto de persistencia (orderPort::create).
     * 4. Mapea Dominio -> DTO (OrderResponse::from).
     */
    public Mono<OrderResponse> registerOrder(OrderRegisterReq req) {
        return Mono.just(req)
                .map(Order::fromRegister)
                .flatMap(orderPort::create)
                .map(OrderResponse::from);
    }

    /**
     * Orquesta la actualización de una orden:
     * 1. Recibe el DTO.
     * 2. Mapea DTO -> Dominio (Order::fromUpdate).
     * 3. Llama al puerto de persistencia (orderPort::update).
     * 4. Mapea Dominio -> DTO (OrderResponse::from).
     */
    public Mono<OrderResponse> updateOrder(OrderUpdateReq req) {
        return Mono.just(req)
                .map(Order::fromUpdate)
                .flatMap(orderPort::update)
                .map(OrderResponse::from);
    }
}
