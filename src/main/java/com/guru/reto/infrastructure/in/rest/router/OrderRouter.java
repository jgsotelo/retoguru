package com.guru.reto.infrastructure.in.rest.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> route(OrderHandler orderHandler) {
        return RouterFunctions.route()
                .GET("/orders", request -> orderHandler.getAllOrders())
                .GET("/orders/{id}", orderHandler::getOrder)
                .build();
    }
}