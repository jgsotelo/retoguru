package com.guru.reto.infrastructure.in;

import com.guru.reto.application.in.port.OrderMutationPort;
import com.guru.reto.application.in.port.OrderSearchPort;
import com.guru.reto.infrastructure.in.configuration.WebFluxConfig;
import com.guru.reto.infrastructure.in.rest.dto.OrderResponse;
import com.guru.reto.infrastructure.in.rest.dto.OrderUpdateReq;
import com.guru.reto.infrastructure.in.rest.router.OrderHandler;
import com.guru.reto.infrastructure.in.rest.router.OrderRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Prueba de integración de la capa Web (Router + Handler).
 * Carga el contexto de WebFlux, importa el Router, el Handler y la Configuración de Validación.
 * Simula (Mock) los puertos de la capa de aplicación.
 */
@WebFluxTest
@Import({OrderRouter.class, OrderHandler.class, WebFluxConfig.class})
class OrderHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderMutationPort orderMutationPort;

    @MockBean
    private OrderSearchPort orderSearchPort;

    /**
     * Prueba el "Happy Path" de la actualización (PUT).
     * Verifica que si el body es válido, se llama al puerto de mutación
     * y se devuelve un HTTP 202 Accepted.
     */
    @Test
    void updateOrder_ShouldReturn202_WhenBodyIsValid() {

        OrderUpdateReq validRequest = new OrderUpdateReq(
                "id-existente-123",
                "Cliente Actualizado",
                "Direccion Nueva"
        );

        OrderResponse mockResponse = OrderResponse.builder()
                .id("id-existente-123")
                .status("ACTUALIZADO")
                .build();

        when(orderMutationPort.updateOrder(any(OrderUpdateReq.class)))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.put().uri("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(OrderResponse.class);
    }
}