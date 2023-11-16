package com.eda.security.utils;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public class MethodUtils {

    public static HttpResponse responseApi(String message, String mapObject, Object object, HttpStatus httpStatus, int statusCode) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of(mapObject, object))
                .message(message)
                .status(httpStatus)
                .statusCode(statusCode)
                .build();
    }
}
