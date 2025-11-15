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

/**
 * Manejador de solicitudes HTTP (Adaptador de Entrada) para la entidad Order.
 * Implementa el patrón de Endpoints Funcionales de WebFlux.
 * Es el equivalente a un @RestController, pero en el paradigma funcional y reactivo.
 */
@Slf4j
@Component
@AllArgsConstructor
public class OrderHandler {

    private final OrderSearchPort orderSearchPort;
    private final OrderMutationPort orderMutationPort;

    /**
     * Busca una orden por su ID.
     * @param request ServerRequest que contiene el path variable {id}.
     * @return Mono<ServerResponse> 200 OK con la orden o 404 Not Found.
     */
    public Mono<ServerResponse> getOrder(ServerRequest request) {
        return Mono.just(request)
                .map(req -> req.pathVariable(Constants.PARAM_ID))
                .flatMap(orderSearchPort::findId)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build())
                .doOnSuccess(res -> log.info("Success Get Order for id"));
    }

    /**
     * Obtiene todas las órdenes (limitado por el adaptador de persistencia).
     * @return Mono<ServerResponse> 200 OK con la lista de órdenes.
     */
    public Mono<ServerResponse> getAllOrders() {
        return orderSearchPort.findAll()
                .flatMap(orders -> ServerResponse.ok().bodyValue(orders))
                .doOnSuccess(res -> log.info("Success All Orders"));
    }

    /**
     * Registra una nueva orden.
     * Lee el cuerpo y automáticamente dispara la validación (configurada en WebFluxConfig).
     * @param request ServerRequest que contiene el Mono<OrderRegisterReq>.
     * @return Mono<ServerResponse> 201 Created o 400 Bad Request si la validación falla.
     */
    public Mono<ServerResponse> registerOrder(ServerRequest request) {
        Mono<OrderRegisterReq> bodyMono = request.bodyToMono(OrderRegisterReq.class);
        return bodyMono
                .flatMap(orderMutationPort::registerOrder)
                .flatMap(order -> ServerResponse.status(201).bodyValue(order))
                .onErrorResume(WebExchangeBindException.class, this::handleValidationException)
                .doOnSuccess(res -> log.info("Success Register Order"));
    }

    /**
     * Actualiza una orden existente.
     * Similar a registerOrder, captura errores de validación automáticamente.
     * @param request ServerRequest que contiene el Mono<OrderUpdateReq>.
     * @return Mono<ServerResponse> 202 Accepted o 400 Bad Request.
     */
    public Mono<ServerResponse> updateOrder(ServerRequest request) {
        return request.bodyToMono(OrderUpdateReq.class)
                .flatMap(orderMutationPort::updateOrder)
                .flatMap(order -> ServerResponse.accepted().bodyValue(order))
                .onErrorResume(WebExchangeBindException.class, this::handleValidationException)
                .doOnSuccess(res -> log.info("Success Update Order"));
    }

    /**
     * Método auxiliar clave para manejar errores de validación.
     * Captura la excepción de WebFlux, la transforma en una lista de ErrorResponse
     * y la devuelve como un 400 Bad Request con un JSON estructurado.
     * @param e La excepción de validación lanzada por WebFlux.
     * @return Mono<ServerResponse> 400 Bad Request con el detalle de los errores.
     */
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