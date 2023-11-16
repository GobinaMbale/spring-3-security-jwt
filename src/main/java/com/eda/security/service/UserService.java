package com.eda.security.service;

import com.eda.security.entity.UserEntity;
import com.eda.security.dto.request.ChangePasswordRequest;

import java.security.Principal;

public interface UserService {
    boolean changePassword(ChangePasswordRequest request, Principal connectedUser);
    UserEntity findById(String email);
}
