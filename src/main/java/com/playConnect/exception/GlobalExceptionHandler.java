package com.playConnect.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.playConnect.Response.ApiResponse;
import com.playConnect.utilities.AppConstants;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
			MethodArgumentNotValidException ex) {

		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		ApiResponse<Map<String, String>> response = new ApiResponse<>();
		response.setMessage("Validation failed");
		response.setData(errors);
		response.setStatus(AppConstants.FAILED);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadRequest(BadRequestException ex) {
		return fail(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Object>> handleUnauthorized(UnauthorizedException ex) {
		return fail(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ApiResponse<Object>> handleForbidden(ForbiddenException ex) {
		return fail(HttpStatus.FORBIDDEN, ex.getMessage());
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiResponse<Object>> handleConflict(ConflictException ex) {
		return fail(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(RegistrationFailedException.class)
	public ResponseEntity<ApiResponse<Object>> handleRegistrationFailed(RegistrationFailedException ex) {
		return fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Object>> handleEmailExists(EmailAlreadyExistsException ex) {
		return fail(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(InvalidUsernamePasswordException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidUserNamePassword(
			InvalidUsernamePasswordException ex) {
		return fail(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
		return fail(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<Object>> handleUnexpected(RuntimeException ex) {
		return fail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private ResponseEntity<ApiResponse<Object>> fail(HttpStatus status, String message) {
		ApiResponse<Object> response = new ApiResponse<>();
		response.setMessage(message);
		response.setData(null);
		response.setStatus(AppConstants.FAILED);
		return ResponseEntity.status(status).body(response);
	}
}
