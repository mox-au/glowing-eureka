package com.pronto.cognosportal.service;

import com.pronto.cognosportal.dto.BulkDeployRequest;
import com.pronto.cognosportal.dto.BulkOperationResponse;
import com.pronto.cognosportal.model.*;
import com.pronto.cognosportal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkOperationService {

    private final BulkOperationRepository bulkOperationRepository;
    private final BulkOperationDetailRepository detailRepository;
    private final CognosServerRepository serverRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final CognosApiService cognosApiService;
    private final AuthService authService;
    private final AuditService auditService;

    @Transactional
    public BulkOperationResponse createBulkOperation(BulkDeployRequest request) {
        User currentUser = authService.getCurrentUser();

        BulkOperation operation = BulkOperation.builder()
                .operationType(request.getOperationType())
                .operationName(request.getOperationName())
                .targetServers(request.getTargetServers())
                .contentPath(request.getContentPath())
                .status(BulkOperation.OperationStatus.PENDING)
                .initiatedBy(currentUser)
                .build();

        operation = bulkOperationRepository.save(operation);

        // Execute asynchronously
        executeBulkDeployAsync(operation.getId(), request);

        auditService.logSuccess(currentUser, "BULK_OPERATION_CREATED", "BULK_OPERATION",
                operation.getId(), null, null);

        return BulkOperationResponse.builder()
                .operationId(operation.getId())
                .status(operation.getStatus().name())
                .targetCount(request.getTargetServers().length)
                .build();
    }

    @Async
    @Transactional
    public void executeBulkDeployAsync(Long operationId, BulkDeployRequest request) {
        BulkOperation operation = bulkOperationRepository.findById(operationId)
                .orElseThrow(() -> new RuntimeException("Operation not found"));

        executeBulkDeploy(operation, request);
    }

    @Transactional
    public void executeBulkDeploy(BulkOperation operation, BulkDeployRequest request) {
        log.info("Starting bulk operation: {}", operation.getId());

        operation.setStatus(BulkOperation.OperationStatus.IN_PROGRESS);
        operation.setStartedAt(LocalDateTime.now());
        bulkOperationRepository.save(operation);

        byte[] content = null;
        if (request.getContentFile() != null) {
            content = Base64.getDecoder().decode(request.getContentFile());
        }

        for (Long serverId : request.getTargetServers()) {
            try {
                CognosServer server = serverRepository.findById(serverId)
                        .orElseThrow(() -> new RuntimeException("Server not found: " + serverId));

                // Deploy content via Cognos API
                if (content != null) {
                    cognosApiService.deployContent(server, content, request.getContentPath());
                }

                // Record success
                recordOperationDetail(operation, server, BulkOperationDetail.DetailStatus.SUCCESS, null);
                operation.incrementSuccessCount();

                // Log to change_history
                logContentDeployment(server, request, operation.getInitiatedBy());

                log.info("Successfully deployed to server: {}", server.getServerName());
            } catch (Exception e) {
                log.error("Failed to deploy to server {}: {}", serverId, e.getMessage());

                CognosServer server = serverRepository.findById(serverId).orElse(null);
                recordOperationDetail(operation, server, BulkOperationDetail.DetailStatus.FAILED, e.getMessage());
                operation.incrementFailureCount();
            }
        }

        operation.setStatus(BulkOperation.OperationStatus.COMPLETED);
        operation.setCompletedAt(LocalDateTime.now());
        bulkOperationRepository.save(operation);

        // Audit log
        auditService.logSuccess(operation.getInitiatedBy(), "BULK_DEPLOY_COMPLETED",
                "BULK_OPERATION", operation.getId(), null, null);

        log.info("Bulk operation completed: {} (Success: {}, Failed: {})",
                operation.getId(), operation.getSuccessCount(), operation.getFailureCount());
    }

    @Transactional
    private void recordOperationDetail(BulkOperation operation, CognosServer server,
                                      BulkOperationDetail.DetailStatus status, String errorMessage) {
        BulkOperationDetail detail = BulkOperationDetail.builder()
                .bulkOperation(operation)
                .server(server)
                .status(status)
                .errorMessage(errorMessage)
                .build();

        detailRepository.save(detail);
    }

    @Transactional
    private void logContentDeployment(CognosServer server, BulkDeployRequest request, User user) {
        Map<String, Object> details = new HashMap<>();
        details.put("operationType", request.getOperationType());
        details.put("contentPath", request.getContentPath());

        ChangeHistory changeHistory = ChangeHistory.builder()
                .server(server)
                .changeType("CONTENT_DEPLOYED")
                .changeDetails(details)
                .changedBy(user)
                .initiatedFrom(ChangeHistory.InitiatedFrom.PORTAL)
                .build();

        changeHistoryRepository.save(changeHistory);
    }
}
