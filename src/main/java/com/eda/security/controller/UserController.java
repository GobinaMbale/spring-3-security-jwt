package com.eda.security.controller;

import com.eda.security.entity.UserEntity;
import com.eda.security.service.UserService;
import com.eda.security.request.ChangePasswordRequest;
import com.eda.security.utils.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<HttpResponse> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        Boolean isSuccess = service.changePassword(request, connectedUser);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("Success", isSuccess))
                        .message("Password Change")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping("/findById/{email}")
    @Cacheable(value = "findUser", key = "#email")
    public ResponseEntity<HttpResponse> findById(@PathVariable String email) {
        UserEntity user = service.findById(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Get One User")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }
}