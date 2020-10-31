package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SignUpRestrictedException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(AuthenticationFailedException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(AuthorizationFailedException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(UpdateCustomerException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SaveAddressException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(CategoryNotFoundException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(RestaurantNotFoundException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(InvalidRatingException exe, WebRequest webRequest){
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }
}
