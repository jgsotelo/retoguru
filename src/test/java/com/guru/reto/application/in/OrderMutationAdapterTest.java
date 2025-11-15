package com.guru.reto.application.in;

import com.guru.reto.application.in.adapter.OrderMutationAdapter;
import com.guru.reto.application.out.port.OrderPort;
import com.guru.reto.domain.Order;
import com.guru.reto.infrastructure.in.rest.dto.OrderRegisterReq;
import com.guru.reto.infrastructure.in.rest.dto.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria para el adaptador de mutación (Capa de Aplicación).
 * Utiliza Mockito para simular el puerto de salida y StepVerifier para probar el Mono.
 */
@ExtendWith(MockitoExtension.class)
class OrderMutationAdapterTest {

    @Mock
    private OrderPort orderPort;

    @InjectMocks
    private OrderMutationAdapter orderMutationAdapter;

    @Test
    void registerOrder_ShouldMapAndCallPort_AndReturnResponse() {

        OrderRegisterReq req = new OrderRegisterReq("Test", "123 Main St", List.of());

        Order mockOrder = Order.builder()
                .orderId("gen-id-123")
                .customerId("Test")
                .status("REGISTRADO")
                .build();

        when(orderPort.create(any(Order.class))).thenReturn(Mono.just(mockOrder));

        StepVerifier.create(orderMutationAdapter.registerOrder(req))
                .expectNextMatches(response ->
                        response.id().equals("gen-id-123") &&
                                response.status().equals("REGISTRADO"))
                .verifyComplete();
    }
}