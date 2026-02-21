package com.fashionstore.service;

import com.fashionstore.dto.request.LoginRequest;
import com.fashionstore.dto.request.RegisterRequest;
import com.fashionstore.dto.response.ApiResponse;
import com.fashionstore.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse login(LoginRequest loginRequest);
    ApiResponse register(RegisterRequest registerRequest);
}
