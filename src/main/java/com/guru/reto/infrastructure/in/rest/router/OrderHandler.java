package com.guru.reto.infrastructure.in.rest.router;

import com.guru.reto.application.in.port.OrderMutationPort;
import com.guru.reto.application.in.port.OrderSearchPort;
import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import com.guru.reto.infrastructure.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class OrderHandler {

    private final OrderSearchPort orderSearchPort;
    private final OrderMutationPort orderMutationPort;

    public Mono<ServerResponse> getOrder(ServerRequest request) {
        return Mono.just(request)
                .map(req -> req.pathVariable(Constants.PARAM_ID))
                .flatMap(orderSearchPort::findId)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSuccess(res -> log.info("Success Get Order for id"));
    }

    public Mono<ServerResponse> getAllOrders() {
        return orderSearchPort.findAll()
                .flatMap(orders -> ServerResponse.ok().bodyValue(orders))
                .doOnSuccess(res -> log.info("Success All Orders"));
    }

    public Mono<ServerResponse> registerOrder(ServerRequest request) {
        return request.bodyToMono(OrderRegisterReq.class)
                .flatMap(orderMutationPort::registerOrder)
                .flatMap(order -> ServerResponse.status(201).bodyValue(order))
                .doOnSuccess(res -> log.info("Success Register Order"));
    }

    public Mono<ServerResponse> updateOrder(ServerRequest request) {
        return request.bodyToMono(OrderUpdateReq.class)
                .flatMap(orderMutationPort::updateOrder)
                .flatMap(order -> ServerResponse.accepted().bodyValue(order))
                .doOnSuccess(res -> log.info("Success Update Order"));
    }
}