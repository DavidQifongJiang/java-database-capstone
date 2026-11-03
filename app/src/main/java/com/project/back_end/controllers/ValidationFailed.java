package com.project.back_end.controllers;
import com.project.back_end.models.*;
import com.project.back_end.services.*;
import com.project.back_end.DTO.Login;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;
import java.util.*;
import java.time.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
@RestControllerAdvice
public class ValidationFailed {
    @ExceptionHandler(ApiException.class) public ResponseEntity<Map<String,Object>> domain(ApiException e) {return ResponseEntity.status(e.getStatus()).body(Map.of("error",e.getMessage()));}
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e) {
        Map<String,String> fields=new TreeMap<>();e.getBindingResult().getFieldErrors().forEach(f->fields.put(f.getField(),f.getDefaultMessage()));
        return ResponseEntity.badRequest().body(Map.of("error","Please check the entered fields","fields",fields));
    }
    @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<Map<String,Object>> conflict() {return ResponseEntity.status(409).body(Map.of("error","A duplicate account or appointment conflicts with this request"));}
    @ExceptionHandler({HttpMessageNotReadableException.class,org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,Object>> malformed() {return ResponseEntity.badRequest().body(Map.of("error","Invalid request format"));}
}
