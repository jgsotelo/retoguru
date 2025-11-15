package com.guru.reto.application.in.port;

import com.guru.reto.domain.Order;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OrderSearchPort {

    Mono<List<Order>> findAll();
    Mono<Order> findId(String id);
}
