package com.eda.security.service.impl;

import com.eda.security.dto.request.AuthenticationRequest;
import com.eda.security.dto.request.VerificationRequest;
import com.eda.security.dto.response.AuthenticationResponse;
import com.eda.security.dto.request.RegisterRequest;
import com.eda.security.entity.enumerated.Role;
import com.eda.security.jwt.JwtService;
import com.eda.security.entity.UserEntity;
import com.eda.security.entity.TokenEntity;
import com.eda.security.repository.TokenRepository;
import com.eda.security.entity.enumerated.TokenType;
import com.eda.security.repository.UserRepository;
import com.eda.security.service.AuthenticationService;
import com.eda.security.tfa.TwoFactorAuthentificationService;
import com.eda.security.validator.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository repository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final ObjectsValidator<RegisterRequest> registerRequestObjectsValidator;
  private final TwoFactorAuthentificationService tfaService;

  @Override
  public AuthenticationResponse register(RegisterRequest request) {
    registerRequestObjectsValidator.validate(request);
    var user = UserEntity.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(request.getRole())
        .build();
    var savedUser = repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  @Override
  public AuthenticationResponse registerAndGenerateQrCode(RegisterRequest request) {
    var user = UserEntity.builder()
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.ADMIN)
            .mfaEnabled(request.isMfaEnabled())
            .build();

    // if MFA enabled --> Generate Secret
    if (request.isMfaEnabled()) {
      user.setSecret(tfaService.generateNewSecret());
    }
    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    return AuthenticationResponse.builder()
            .secretImageUri(tfaService.generateQrCodeImageUri(user.getSecret()))
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .mfaEnabled(user.isMfaEnabled())
            .build();
  }

  @Override
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    if (user.isMfaEnabled()) {
      return AuthenticationResponse.builder()
              .accessToken("")
              .refreshToken("")
              .mfaEnabled(true)
              .build();
    }
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .refreshToken(refreshToken)
            .mfaEnabled(false)
            .build();
  }

  private void saveUserToken(UserEntity userEntity, String jwtToken) {
    var token = TokenEntity.builder()
        .userEntity(userEntity)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(UserEntity userEntity) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(userEntity.getId());
    if (validUserTokens.isEmpty())
      return;
    validUserTokens.forEach(tokenEntity -> {
      tokenEntity.setExpired(true);
      tokenEntity.setRevoked(true);
    });
    tokenRepository.saveAll(validUserTokens);
  }

  @Override
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userEmail = jwtService.extractUsername(refreshToken);
    if (userEmail != null) {
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }

  @Override
  public AuthenticationResponse verifyCode(VerificationRequest verificationRequest) {
    UserEntity user = repository
            .findByEmail(verificationRequest.getEmail())
            .orElseThrow(() -> new EntityNotFoundException(
                    String.format("No user found with %S", verificationRequest.getEmail()))
            );
    if (tfaService.isOtpNotValid(user.getSecret(), verificationRequest.getCode())) {
      throw new BadCredentialsException("Code is not correct");
    }
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .mfaEnabled(user.isMfaEnabled())
            .build();
  }
}
