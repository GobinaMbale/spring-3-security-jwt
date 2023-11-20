package com.eda.security.dto.request;

import com.eda.security.entity.enumerated.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  @NotEmpty(message = "The first name should not be empty")
  @NotNull(message = "The first name should not be not null")
  private String firstname;
  @NotEmpty(message = "The last name should not be empty")
  @NotNull(message = "The last name should not be not null")
  private String lastname;
  @NotEmpty(message = "The email should not be empty")
  @NotNull(message = "The email should not be not null")
  private String email;
  @NotEmpty(message = "The password should not be empty")
  @NotNull(message = "The password should not be not null")
  private String password;
  private Role role;
  private boolean mfaEnabled;
}
