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

@Service
@AllArgsConstructor
public class OrderMutationAdapter implements OrderMutationPort {

    private final OrderPort orderPort;

    public Mono<OrderResponse> registerOrder(OrderRegisterReq req) {
        return Mono.just(req)
                .map(Order::fromRegister)
                .flatMap(orderPort::create)
                .map(OrderResponse::from);
    }

    public Mono<OrderResponse> updateOrder(OrderUpdateReq req) {
        return Mono.just(req)
                .map(Order::fromUpdate)
                .flatMap(orderPort::update)
                .map(OrderResponse::from);
    }
}
