package com.guru.reto.infrastructure.in.configuration;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guru.reto.infrastructure.util.ErrorResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
public class ValidationFilter {

    private final Validator validator;

    public ValidationFilter(Validator validator) {
        this.validator = validator;
    }

    public <T> HandlerFilterFunction<ServerResponse, ServerResponse> validate(Class<T> bodyType) {
        return (ServerRequest request, HandlerFunction<ServerResponse> next) -> {
            Mono<T> bodyMono = request.bodyToMono(bodyType)
                    .doOnNext(obj -> {
                        Errors errors = new BeanPropertyBindingResult(obj, obj.getClass().getSimpleName());
                        validator.validate(obj, errors);

                        if (errors.hasErrors()) {
                            var err = errors.getAllErrors().stream()
                                    .map(error -> ErrorResponse.builder()
                                            .field(error.getObjectName())
                                            .message(error.getDefaultMessage())
                                            .build())
                                    .toList();
                            throw new ServerWebInputException(new Gson().toJson(err), null, null);
                        }
                    })
                    .switchIfEmpty(Mono.error(new ServerWebInputException("Cuerpo de solicitud vacío")));

            return bodyMono
                    .flatMap(validatedBody -> {
                        request.attributes().put("validatedBody", validatedBody);
                        return next.handle(request);
                    })
                    .onErrorResume(ServerWebInputException.class, e ->
                            ServerResponse.badRequest()
                                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                    .bodyValue(new Gson().fromJson(e.getReason(), new TypeToken<List<ErrorResponse>>(){}.getType()))
                    );
        };
    }
}