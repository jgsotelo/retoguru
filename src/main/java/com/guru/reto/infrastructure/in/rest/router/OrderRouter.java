package com.guru.reto.infrastructure.in.rest.router;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@AllArgsConstructor
public class OrderRouter {

    private static final String PATH_ORDER = "/orders";
    private static final String PATH_ORDER_KEY = "/orders";

    @Bean
    public RouterFunction<ServerResponse> route(OrderHandler orderHandler) {
        return RouterFunctions.route()
                .GET(PATH_ORDER, request -> orderHandler.getAllOrders())
                .GET(PATH_ORDER_KEY, orderHandler::getOrder)
                .POST(PATH_ORDER, orderHandler::registerOrder)
                .PUT(PATH_ORDER, orderHandler::updateOrder)
                .build();
    }
}