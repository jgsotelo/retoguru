package com.guru.reto.infrastructure.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@UtilityClass
public class Constants {

    public static final String PARAM_ID = "id";
    public static final String STATUS_PENDING = "PENDIENTE";
    public static final String STATUS_REGISTRATION = "REGISTRADO";
    public static final String MSG_ORDER_NOT_PROCESSED = "Pedido no procesado";
    public static final String SEPARATE_UUID = "-";

    public static String generateId() {
        return UUID.randomUUID().toString().replace(SEPARATE_UUID, StringUtils.EMPTY);

    }
}
