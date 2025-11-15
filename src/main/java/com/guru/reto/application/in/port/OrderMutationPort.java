package com.guru.reto.application.in.port;

import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderResponse;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import reactor.core.publisher.Mono;

public interface OrderMutationPort {

    Mono<OrderResponse> registerOrder(OrderRegisterReq req);
    Mono<OrderResponse> updateOrder(OrderUpdateReq req);
}
