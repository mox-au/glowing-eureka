package com.pronto.cognosportal.service;

import com.pronto.cognosportal.model.AuditLog;
import com.pronto.cognosportal.model.User;
import com.pronto.cognosportal.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(User user, String action, String targetEntity, Long entityId,
                    String ipAddress, Map<String, Object> details, AuditLog.AuditResult result) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .user(user)
                    .action(action)
                    .targetEntity(targetEntity)
                    .entityId(entityId)
                    .ipAddress(ipAddress)
                    .details(details)
                    .result(result)
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: user={}, action={}, result={}",
                     user != null ? user.getUsername() : "system", action, result);
        } catch (Exception e) {
            // Don't fail the operation if audit logging fails
            log.error("Failed to create audit log", e);
        }
    }

    @Transactional
    public void logSuccess(User user, String action, String targetEntity, Long entityId,
                          String ipAddress, Map<String, Object> details) {
        log(user, action, targetEntity, entityId, ipAddress, details, AuditLog.AuditResult.SUCCESS);
    }

    @Transactional
    public void logFailure(User user, String action, String targetEntity, Long entityId,
                          String ipAddress, Map<String, Object> details) {
        log(user, action, targetEntity, entityId, ipAddress, details, AuditLog.AuditResult.FAILURE);
    }
}
