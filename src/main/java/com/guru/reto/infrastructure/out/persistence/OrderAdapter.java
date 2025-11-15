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

import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class OrderAdapter implements OrderPort {

    private final DynamoDbAsyncTable<Order> orderTable;

    public Mono<List<Order>> findAll() {
        return Flux.from(orderTable.scan().items().limit(10)
                        .filter(order -> StringUtils.isNotEmpty(order.getStatus()) && order.getStatus().equals(Constants.STATUS_REGISTRATION)))
                .collectList()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.findAll"))
                .doOnError(err -> log.error("Error OrderAdapter.findAll: {}", err.getMessage()));
    }

    public Mono<Order> findId(String id) {
        return Mono.just(id)
                .map(key -> Key.builder().partitionValue(id).build())
                .flatMap(key -> Mono.fromCompletionStage(orderTable.getItem(key)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.findId: {}", id))
                .doOnError(err -> log.error("Error OrderAdapter.findId: {} - {}", id, err.getMessage()));
    }

    public Mono<Order> create(Order order) {
        return Mono.just(order.toBuilder().status(Constants.STATUS_REGISTRATION).build())
                .flatMap(body -> Mono.fromCompletionStage(orderTable.putItem(body))
                        .onErrorResume(err -> Mono.error(new Throwable(Constants.MSG_ORDER_NOT_PROCESSED))))
                .thenReturn(order.toBuilder().status(Constants.STATUS_REGISTRATION).build())
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(rst -> log.info("Success OrderAdapter.create: {}", order.getOrderId()))
                .doOnError(err -> log.error("Error OrderAdapter.create: {} - {}", order.getOrderId(), err.getMessage()));
    }

    public Mono<Order> update(Order order) {
        return Mono.just(order)
                .map(body -> Key.builder().partitionValue(body.getOrderId()).build())
                .flatMap(id -> Mono.fromCompletionStage(orderTable.getItem(id))
                        .switchIfEmpty(Mono.error(new Throwable(Constants.MSG_ORDER_NOT_PROCESSED))))
                .map(orderCurrent -> UpdateItemEnhancedRequest.builder(Order.class)
                        .item(orderCurrent.toBuilder()
                                .customerId(StringUtils.isNotEmpty(order.getCustomerId()) ? order.getCustomerId() : orderCurrent.getCustomerId())
                                .address(StringUtils.isNotEmpty(order.getAddress()) ? order.getAddress() : orderCurrent.getAddress())
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
