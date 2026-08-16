package com.odisha.floodrelief.exception;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {

    private final HttpStatus status;

    public UnauthorizedException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
