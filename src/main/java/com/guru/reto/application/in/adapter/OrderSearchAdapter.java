package com.guru.reto.application.in.adapter;

import com.guru.reto.application.in.port.OrderSearchPort;
import com.guru.reto.application.out.port.OrderPort;
import com.guru.reto.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderSearchAdapter implements OrderSearchPort {

    private final OrderPort orderPort;

    public Mono<List<Order>> findAll() {
        return orderPort.findAll();
    }

    public Mono<Order> findId(String id) {
        return orderPort.findId(id);
    }
}
