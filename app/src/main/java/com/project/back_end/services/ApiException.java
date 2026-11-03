package com.project.back_end.services;
import org.springframework.http.HttpStatus;
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    public ApiException(HttpStatus status, String message) {super(message); this.status=status;}
    public HttpStatus getStatus() {return status;}
}
