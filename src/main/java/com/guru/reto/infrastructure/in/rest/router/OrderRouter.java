package com.guru.reto.infrastructure.in.rest.router;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Define las rutas (endpoints) de la API REST para Pedidos.
 * Utiliza el patrón de Endpoints Funcionales de Spring WebFlux.
 */
@Configuration
@AllArgsConstructor
public class OrderRouter {

    private static final String PATH_ORDER = "/orders";
    private static final String PATH_ORDER_KEY = "/orders/{id}";

    /**
     * Bean que configura el enrutamiento funcional.
     * Mapea las rutas y métodos HTTP a los métodos correspondientes del OrderHandler.
     * @param orderHandler El manejador que contiene la lógica de la solicitud.
     * @return Un RouterFunction que Spring WebFlux usará para enrutar las peticiones.
     */
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