package com.guru.reto.infrastructure.out;

import com.guru.reto.domain.Order;
import com.guru.reto.infrastructure.out.persistence.OrderAdapter;
import com.guru.reto.infrastructure.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria para el adaptador de persistencia (Capa de Infraestructura).
 * Simula el comportamiento de DynamoDbAsyncTable y verifica la lógica reactiva
 * (manejo de CompletableFuture, switchIfEmpty, onErrorResume).
 */
@ExtendWith(MockitoExtension.class)
class OrderAdapterTest {

    @Mock
    private DynamoDbAsyncTable<Order> orderTable;

    @InjectMocks
    private OrderAdapter orderAdapter;

    @Test
    void findId_ShouldReturnOrder_WhenFound() {

        String orderId = "123";
        Order mockOrder = Order.builder().orderId(orderId).build();

        when(orderTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(mockOrder));

        Mono<Order> resultMono = orderAdapter.findId(orderId);

        StepVerifier.create(resultMono)
                .expectNext(mockOrder)
                .verifyComplete();
    }

    @Test
    void findId_ShouldReturnEmpty_WhenNotFound() {

        String orderId = "404";

        when(orderTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(orderAdapter.findId(orderId))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void create_ShouldReturnOrder_WhenSuccessful() {

        Order order = Order.builder().orderId("new-id").build();

        when(orderTable.putItem(any(Order.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(orderAdapter.create(order))
                .expectNextMatches(savedOrder ->
                        savedOrder.getOrderId().equals("new-id") &&
                                savedOrder.getStatus().equals(Constants.STATUS_REGISTRATION))
                .verifyComplete();
    }

    @Test
    void create_ShouldReturnError_WhenPutFails() {

        Order order = Order.builder().orderId("new-id").build();

        when(orderTable.putItem(any(Order.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("DB Error")));

        StepVerifier.create(orderAdapter.create(order))
                .expectErrorMessage(Constants.MSG_ORDER_NOT_PROCESSED)
                .verify();
    }

    @Test
    void update_ShouldReturnError_WhenItemIsNotFound() {

        Order orderToUpdate = Order.builder().orderId("404").customerId("Cliente B").build();

        when(orderTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(orderAdapter.update(orderToUpdate))
                .expectErrorMessage(Constants.MSG_ORDER_NOT_PROCESSED)
                .verify();
    }

    @Test
    void update_ShouldMergeAndReturnOrder_WhenSuccessful() {

        Order orderUpdateData = Order.builder()
                .orderId("123")
                .customerId("Cliente Nuevo")
                .address("Direccion Nueva")
                .build();

        Order orderCurrentData = Order.builder()
                .orderId("123")
                .customerId("Cliente Antiguo")
                .address("Direccion Antigua")
                .version(1L)
                .status("REGISTRADO")
                .build();

        when(orderTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(orderCurrentData));

        when(orderTable.updateItem(any(UpdateItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(orderUpdateData));

        StepVerifier.create(orderAdapter.update(orderUpdateData))
                .expectNext(orderUpdateData)
                .verifyComplete();
    }
}