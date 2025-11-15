package com.guru.reto.application.in;

import com.guru.reto.application.in.adapter.OrderSearchAdapter;
import com.guru.reto.application.out.port.OrderPort;
import com.guru.reto.domain.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier; // Importamos StepVerifier

import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Prueba unitaria para OrderSearchAdapter (Capa de Aplicación).
 * Aísla el adaptador y simula (Mock) el puerto de salida (OrderPort).
 * Utiliza StepVerifier para validar los flujos reactivos (Mono/Flux).
 */
@ExtendWith(MockitoExtension.class)
class OrderSearchAdapterTest {

    @Mock
    private OrderPort orderPort;

    @InjectMocks
    private OrderSearchAdapter orderSearchAdapter;

    /**
     * Prueba el caso de uso findAll.
     * Verifica que el adaptador llame al puerto y devuelva la lista esperada.
     */
    @Test
    void findAll_ShouldReturnListOfOrders() {

        Order order1 = Order.builder().orderId("1").customerId("Cliente 1").build();
        Order order2 = Order.builder().orderId("2").customerId("Cliente 2").build();
        List<Order> mockList = List.of(order1, order2);

        when(orderPort.findAll()).thenReturn(Mono.just(mockList));

        StepVerifier.create(orderSearchAdapter.findAll())
                .expectNextMatches(list ->
                        list.size() == 2 &&
                                list.get(0).getCustomerId().equals("Cliente 1"))
                .verifyComplete();
    }

    /**
     * Prueba el caso de uso findId cuando la orden SÍ se encuentra.
     */
    @Test
    void findId_ShouldReturnOrder_WhenFound() {

        String orderId = "123";
        Order mockOrder = Order.builder().orderId(orderId).customerId("Cliente Test").build();

        when(orderPort.findId(orderId)).thenReturn(Mono.just(mockOrder));

        StepVerifier.create(orderSearchAdapter.findId(orderId))
                .expectNext(mockOrder)
                .verifyComplete();
    }

    /**
     * Prueba el caso de uso findId cuando la orden NO se encuentra.
     * El adaptador debe devolver un Mono vacío.
     */
    @Test
    void findId_ShouldReturnEmpty_WhenNotFound() {

        String orderId = "404";

        when(orderPort.findId(orderId)).thenReturn(Mono.empty());

        StepVerifier.create(orderSearchAdapter.findId(orderId))
                .expectNextCount(0)
                .verifyComplete();
    }
}