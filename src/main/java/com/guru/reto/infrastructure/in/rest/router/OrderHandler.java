package com.guru.reto.infrastructure.in.rest.router;

import com.guru.reto.domain.Order;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Component
@AllArgsConstructor
public class OrderHandler {

    private final DynamoDbAsyncTable<Order> orderTable;

    public Mono<ServerResponse> getOrder(ServerRequest request) {
        String id = request.pathVariable("id");
        Key key = Key.builder().partitionValue(id).build();

        Mono<Order> itemMono = Mono.fromCompletionStage(orderTable.getItem(key));

        return itemMono
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllOrders() {
        // NOTA: scan() es costoso y lento para tablas grandes. Usar Query o GSI para producción.
        return ServerResponse.ok().body(orderTable.scan().items(), Order.class);
    }
}