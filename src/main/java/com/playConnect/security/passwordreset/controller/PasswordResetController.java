package com.playConnect.security.passwordreset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playConnect.Response.ApiResponse;
import com.playConnect.security.passwordreset.dto.ForgotPasswordRequest;
import com.playConnect.security.passwordreset.dto.ResetPasswordRequest;
import com.playConnect.security.passwordreset.service.PasswordResetService;
import com.playConnect.utilities.AppConstants;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

  private final PasswordResetService passwordResetService;

  public PasswordResetController(PasswordResetService passwordResetService) {
    this.passwordResetService = passwordResetService;
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse<Object>> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request
  ) {
    passwordResetService.sendResetLink(request.getEmail());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setStatus(AppConstants.SUCCESS);
    response.setMessage("If the email exists, a password reset link has been sent.");
    response.setData(null);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<Object>> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request
  ) {
    passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

    ApiResponse<Object> response = new ApiResponse<>();
    response.setStatus(AppConstants.SUCCESS);
    response.setMessage("Password reset successful.");
    response.setData(null);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}

