package com.guru.reto.infrastructure.out.persistence;

import com.guru.reto.application.out.port.OrderPort;
import com.guru.reto.domain.Order;
import com.guru.reto.infrastructure.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.time.Instant;
import java.util.List;

/**
 * Adaptador de Salida (Outbound Adapter).
 * Implementa la interfaz OrderPort y maneja la comunicación directa
 * con la infraestructura de base de datos (AWS DynamoDB).
 * Utiliza el cliente Asíncrono Mejorado de DynamoDB.
 */
@Slf4j
@Repository
@AllArgsConstructor
public class OrderAdapter implements OrderPort {

    private final DynamoDbAsyncTable<Order> orderTable;

    /**
     * Busca todas las órdenes (limitado a 10) con estado 'REGISTRADO'.
     * @return Un Flux (reactivo) que emite las órdenes y se colecta en una Lista.
     */
    public Mono<List<Order>> findAll() {
        return Flux.from(orderTable.scan().items().limit(10)
                        .filter(order -> StringUtils.isNotEmpty(order.getStatus()) && order.getStatus().equals(Constants.STATUS_REGISTRATION)))
                .collectList()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.findAll"))
                .doOnError(err -> log.error("Error OrderAdapter.findAll: {}", err.getMessage()));
    }

    /**
     * Busca una orden por su Clave de Partición (ID).
     * @param id El orderId.
     * @return Un Mono<Order> o Mono.empty() si no se encuentra.
     */
    public Mono<Order> findId(String id) {
        return Mono.just(id)
                .map(key -> Key.builder().partitionValue(id).build())
                .flatMap(key -> Mono.fromCompletionStage(orderTable.getItem(key)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.findId: {}", id))
                .doOnError(err -> log.error("Error OrderAdapter.findId: {} - {}", id, err.getMessage()));
    }

    /**
     * Guarda una nueva entidad Order en DynamoDB.
     * @param order El objeto de dominio a persistir.
     * @return El objeto guardado (con el estado actualizado).
     */
    public Mono<Order> create(Order order) {
        return Mono.just(order.toBuilder().status(Constants.STATUS_REGISTRATION).build())
                .flatMap(body -> Mono.fromCompletionStage(orderTable.putItem(body))
                        .onErrorResume(err -> Mono.error(new Throwable(Constants.MSG_ORDER_NOT_PROCESSED))))
                .thenReturn(order.toBuilder().status(Constants.STATUS_REGISTRATION).build())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.create: {}", order.getOrderId()))
                .doOnError(err -> log.error("Error OrderAdapter.create: {} - {}", order.getOrderId(), err.getMessage()));
    }

    /**
     * Actualiza una orden existente en DynamoDB.
     * Utiliza un patrón de "leer y luego escribir" (Read-Then-Write) para fusionar campos.
     * @param order El objeto de dominio con los campos a actualizar.
     * @return El objeto actualizado.
     */
    public Mono<Order> update(Order order) {
        return Mono.just(order)
                .map(body -> Key.builder().partitionValue(body.getOrderId()).build())
                .flatMap(id -> Mono.fromCompletionStage(orderTable.getItem(id))
                        .switchIfEmpty(Mono.error(new Throwable(Constants.MSG_ORDER_NOT_PROCESSED))))
                .map(orderCurrent -> UpdateItemEnhancedRequest.builder(Order.class)
                        .item(orderCurrent.toBuilder()
                                .customerId(StringUtils.isNotEmpty(order.getCustomerId()) ? order.getCustomerId() : orderCurrent.getCustomerId())
                                .address(StringUtils.isNotEmpty(order.getAddress()) ? order.getAddress() : orderCurrent.getAddress())
                                .orderDate(orderCurrent.getOrderDate())
                                .orderUpdate(Instant.now())
                                .version(orderCurrent.getVersion())
                                .status(orderCurrent.getStatus())
                                .items(orderCurrent.getItems())
                                .build())
                        .build())
                .flatMap(body -> Mono.fromCompletionStage(orderTable.updateItem(body))
                        .switchIfEmpty(Mono.error(new Throwable(Constants.MSG_ORDER_NOT_PROCESSED))))
                .thenReturn(order)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.update: {}", order.getOrderId()))
                .doOnError(err -> log.error("Error OrderAdapter.update: {} - {}", order.getOrderId(), err.getMessage()));
    }
}
