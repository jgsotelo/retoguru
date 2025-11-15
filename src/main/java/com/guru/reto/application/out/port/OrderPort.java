package com.guru.reto.application.out.port;

import com.guru.reto.domain.Order;
import reactor.core.publisher.Mono;
import java.util.List;

public interface OrderPort {

    Mono<List<Order>> findAll();
    Mono<Order> findId(String id);
    Mono<Order> create(Order order);
    Mono<Order> update(Order order);
}