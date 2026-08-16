package com.odisha.floodrelief.logging;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Logs every API request/response (method, path, status, duration).
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldSkip(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long tookMs = System.currentTimeMillis() - start;
            String user = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous";
            log.info("HTTP {} {} -> {} | user={} | {} ms",
                    request.getMethod(),
                    request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""),
                    wrappedResponse.getStatus(),
                    user,
                    tookMs);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/swagger")
                || path.contains("/v2/api-docs")
                || path.contains("/webjars")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".ico");
    }
}
