package com.eda.security.controller;

import com.eda.security.request.AuthenticationRequest;
import com.eda.security.auth.AuthenticationResponse;
import com.eda.security.service.AuthenticationService;
import com.eda.security.request.RegisterRequest;
import com.eda.security.utils.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<HttpResponse> register(
      @RequestBody RegisterRequest request
  ) {
    AuthenticationResponse response = service.register(request);
    return ResponseEntity.created(URI.create("")).body(
            HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .data(Map.of("register", response))
                    .message("User Register")
                    .status(HttpStatus.CREATED)
                    .statusCode(HttpStatus.CREATED.value())
                    .build()
    );
  }
  @PostMapping("/authenticate")
  public ResponseEntity<HttpResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    AuthenticationResponse response = service.authenticate(request);
    return ResponseEntity.ok().body(
            HttpResponse.builder()
                    .timeStamp(LocalDateTime.now().toString())
                    .data(Map.of("authenticate", response))
                    .message("Authentication with success")
                    .status(HttpStatus.OK)
                    .statusCode(HttpStatus.OK.value())
                    .build()
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
