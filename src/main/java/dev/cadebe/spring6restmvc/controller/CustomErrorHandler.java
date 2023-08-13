package dev.cadebe.spring6restmvc.controller;

import jakarta.persistence.RollbackException;
import jakarta.validation.ConstraintViolationException;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<List<Map<String, String>>> handleBindErrors(MethodArgumentNotValidException exception) {
        val errors = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.toString(), fieldError.getDefaultMessage());
                    return errorMap;
                }).toList();

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<List<Map<String, String>>> handleJpaValidationErrors(TransactionSystemException exception) {
        ResponseEntity.BodyBuilder responseEntityBuilder = ResponseEntity.badRequest();

        if (exception.getCause().getCause() instanceof ConstraintViolationException cve) {
            val errors = cve.getConstraintViolations().stream()
                    .map(constraintViolation -> {
                        Map<String, String> errorMap = new HashMap<>();
                        errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
                        return errorMap;
                    })
                    .toList();

            return responseEntityBuilder.body(errors);
        }

        return responseEntityBuilder.build();
    }
}
