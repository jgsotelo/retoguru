package com.guru.reto.infrastructure.in.rest.router;

import com.guru.reto.infrastructure.in.configuration.ValidationFilter;
import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@AllArgsConstructor
public class OrderRouter {

    private final ValidationFilter validationFilter;
    private static final String PATH_ORDER = "/orders";
    private static final String PATH_ORDER_KEY = "/orders/{id}";

    @Bean
    public RouterFunction<ServerResponse> route(OrderHandler orderHandler) {
        RouterFunction<ServerResponse> nonFilteredRoutes = RouterFunctions.route()
                .GET(PATH_ORDER, request -> orderHandler.getAllOrders())
                .GET(PATH_ORDER_KEY, orderHandler::getOrder)
                .build();

        RouterFunction<ServerResponse> postRoute = RouterFunctions.route()
                .POST(PATH_ORDER, orderHandler::registerOrder)
                .filter(validationFilter.validate(OrderRegisterReq.class))
                .build();

        RouterFunction<ServerResponse> putRoute = RouterFunctions.route()
                .PUT(PATH_ORDER, orderHandler::updateOrder)
                .filter(validationFilter.validate(OrderUpdateReq.class))
                .build();

        return nonFilteredRoutes
                .and(postRoute)
                .and(putRoute);
    }
}