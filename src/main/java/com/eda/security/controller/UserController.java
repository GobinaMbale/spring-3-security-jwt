package com.eda.security.controller;

import com.eda.security.entity.UserEntity;
import com.eda.security.service.UserService;
import com.eda.security.dto.request.ChangePasswordRequest;
import com.eda.security.utils.HttpResponse;
import com.eda.security.utils.MethodUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<HttpResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Principal connectedUser
    ) {
        Boolean isSuccess = service.changePassword(request, connectedUser);
        return ResponseEntity.ok().body(
                MethodUtils.responseApi("Password Change", "success",
                        isSuccess, HttpStatus.OK, HttpStatus.OK.value())
        );
    }

    @GetMapping("/findById/{email}")
    @Cacheable(value = "findUser", key = "#email")
    public ResponseEntity<HttpResponse> findById(@PathVariable String email) {
        UserEntity user = service.findById(email);
        return ResponseEntity.ok().body(
                MethodUtils.responseApi("Get One User", "user",
                        user, HttpStatus.OK, HttpStatus.OK.value())
        );
    }
}