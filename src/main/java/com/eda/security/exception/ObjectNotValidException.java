package com.eda.security.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ObjectNotValidException extends RuntimeException {

    private final Set<String> errorMesdages;
}
