package com.upgrad.quora.api.exception;

import com.upgrad.quora.api.model.ErrorResponse;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signupRestrictedException(final SignUpRestrictedException exe,
                                                                   final WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(final AuthenticationFailedException exe,
                                                                       final WebRequest request) {
        return new ResponseEntity<>(
                new ErrorResponse().code(exe.getCode()).message(exe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationFailedException(final AuthorizationFailedException exe,
                                                                            final WebRequest request) {
        final ErrorResponse error = new ErrorResponse().code(exe.getCode())
                                                       .message(exe.getErrorMessage())
                                                       .rootCause(exe.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(InvalidQuestionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQuestionException(final InvalidQuestionException exe,
                                                                        final WebRequest request) {
        final ErrorResponse error = new ErrorResponse().code(exe.getCode())
                                                       .message(exe.getErrorMessage())
                                                       .rootCause(exe.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(final UserNotFoundException exe,
                                                                     final WebRequest request) {
        final ErrorResponse error = new ErrorResponse().code(exe.getCode())
                                                       .message(exe.getErrorMessage())
                                                       .rootCause(exe.getErrorMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
