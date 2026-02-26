package com.franchise.domain.util;

import reactor.core.publisher.Mono;
import java.util.Objects;

public final class ValidationHelper {
    private ValidationHelper() {}

    public static void requireNotNull(Object obj, String name) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException(String.format(DomainConstants.ERROR_NULL_ARGUMENT, name));
        }
    }

    public static <T> Mono<T> onErrorNotFound(Long id, String template) {
        return Mono.error(new IllegalArgumentException(String.format(template, id)));
    }
}