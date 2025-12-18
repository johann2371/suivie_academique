package com.eadl.suivi_academique.controllers.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eadl.suivi_academique.services.exceptions.coursexception.CoursNotFoundException;
import com.eadl.suivi_academique.services.exceptions.coursexception.InvalidCoursException;
import com.eadl.suivi_academique.services.exceptions.salleexception.InvalidSalleException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCoursException.class)
    public ResponseEntity<?> handleInvalidCoursException(InvalidCoursException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(CoursNotFoundException.class)
    public ResponseEntity<?> handleCoursNotFound(CoursNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(InvalidSalleException.class)
    public ResponseEntity<?> handleInvalidSalleException(InvalidSalleException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // fallback général
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne du serveur : " + ex.getMessage());
    }

}
