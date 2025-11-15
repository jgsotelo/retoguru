package com.guru.reto.infrastructure.in.rest.router;

import com.guru.reto.application.in.port.OrderMutationPort;
import com.guru.reto.application.in.port.OrderSearchPort;
import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import com.guru.reto.infrastructure.util.Constants;
import com.guru.reto.infrastructure.util.ErrorResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import java.util.List;

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
        Mono<OrderRegisterReq> bodyMono = request.bodyToMono(OrderRegisterReq.class);
        return bodyMono
                .flatMap(orderMutationPort::registerOrder)
                .flatMap(order -> ServerResponse.status(201).bodyValue(order))
                .onErrorResume(WebExchangeBindException.class, this::handleValidationException)
                .doOnSuccess(res -> log.info("Success Register Order"));
    }

    public Mono<ServerResponse> updateOrder(ServerRequest request) {
        return request.bodyToMono(OrderUpdateReq.class)
                .flatMap(orderMutationPort::updateOrder)
                .flatMap(order -> ServerResponse.accepted().bodyValue(order))
                .onErrorResume(WebExchangeBindException.class, this::handleValidationException)
                .doOnSuccess(res -> log.info("Success Update Order"));
    }

    private Mono<ServerResponse> handleValidationException(WebExchangeBindException e) {
        List<ErrorResponse> errors = e.getBindingResult().getAllErrors().stream()
                .map(error -> ErrorResponse.builder()
                        .field(error.getObjectName())
                        .message(error.getDefaultMessage())
                        .build())
                .toList();

        return ServerResponse.badRequest()
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue(errors);
    }
}