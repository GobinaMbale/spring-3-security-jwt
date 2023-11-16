package com.eda.security.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public class ObjectNotValidException extends RuntimeException {
    private final Set<String> errorMessages;
}
