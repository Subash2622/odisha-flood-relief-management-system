package com.odisha.floodrelief.util;

// Subash Chandra Sahoo
// Software Engineer
// Odisha Flood Relief & NGO Management System
// Copyright (c) 2026 Subash Chandra Sahoo. All rights reserved.

import com.odisha.floodrelief.entity.AuditLog;
import com.odisha.floodrelief.entity.User;
import com.odisha.floodrelief.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogUtil {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, String action, String entityType, Long entityId, String details) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }
}
