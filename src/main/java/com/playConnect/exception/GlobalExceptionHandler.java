package com.playConnect.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.playConnect.Response.ApiResponse;
import com.playConnect.utilities.AppConstants;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return errors;
    }
    
    @ExceptionHandler(RegistrationFailedException.class)
    public ResponseEntity<ApiResponse<Object>> handleRegistrationFailed(
            RegistrationFailedException ex) {

        ApiResponse<Object> response = new ApiResponse<>();
        response.setMessage(ex.getMessage());
 		response.setData(null);
 		response.setStatus(AppConstants.FAILED);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
                .body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneric(
            RuntimeException ex) {

    	ApiResponse<Object> response = new ApiResponse<>();
 		response.setMessage(ex.getMessage());
 		response.setData(null);
 		response.setStatus(AppConstants.FAILED);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleEmailExists(
            EmailAlreadyExistsException ex) {

    	ApiResponse<Object> response = new ApiResponse<>();
    	response.setMessage(ex.getMessage());
 		response.setData(null);
 		response.setStatus(AppConstants.FAILED);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)   // 409
                .body(response);
    }
    
    @ExceptionHandler(InvalidUsernamePasswordException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidUserNamePassword(
            InvalidUsernamePasswordException ex) {

    	ApiResponse<Object> response = new ApiResponse<>();
    	response.setMessage(ex.getMessage());
 		response.setData(null);
 		response.setStatus(AppConstants.FAILED);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)   // 401
                .body(response);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
    		ResourceNotFoundException ex) {

    	ApiResponse<Object> response = new ApiResponse<>();
    	response.setMessage(ex.getMessage());
 		response.setData(null);
 		response.setStatus(AppConstants.FAILED);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)   // 401
                .body(response);
    }
}