package com.eda.security.controller;

import com.eda.security.dto.request.AuthenticationRequest;
import com.eda.security.dto.response.AuthenticationResponse;
import com.eda.security.service.AuthenticationService;
import com.eda.security.dto.request.RegisterRequest;
import com.eda.security.utils.HttpResponse;
import com.eda.security.utils.MethodUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<HttpResponse> register(
          @RequestBody @Valid RegisterRequest request
  ) {
    AuthenticationResponse response = service.register(request);
    return ResponseEntity.created(URI.create("")).body(
            MethodUtils.responseApi("User Register", "register",
                    response, HttpStatus.CREATED, HttpStatus.CREATED.value())
    );
  }

  @PostMapping("/authenticate")
  public ResponseEntity<HttpResponse> authenticate(
          @RequestBody @Valid AuthenticationRequest request
  ) {
    AuthenticationResponse response = service.authenticate(request);
    return ResponseEntity.ok().body(
            MethodUtils.responseApi("Authentication with success", "authenticate",
                    response, HttpStatus.OK, HttpStatus.OK.value())
    );
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
