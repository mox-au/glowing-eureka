package com.pronto.cognosportal.service;

import com.pronto.cognosportal.dto.ServerDTO;
import com.pronto.cognosportal.dto.ServerEnrollmentRequest;
import com.pronto.cognosportal.dto.ServerUpdateRequest;
import com.pronto.cognosportal.model.CognosServer;
import com.pronto.cognosportal.model.User;
import com.pronto.cognosportal.repository.CognosServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final CognosServerRepository serverRepository;
    private final EncryptionService encryptionService;
    private final AuthService authService;
    private final AuditService auditService;

    public List<ServerDTO> getAllServers() {
        return serverRepository.findAll().stream()
                .map(ServerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ServerDTO> getActiveServers() {
        return serverRepository.findByIsActiveTrue().stream()
                .map(ServerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ServerDTO getServerById(Long id) {
        CognosServer server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));
        return ServerDTO.fromEntity(server);
    }

    @Transactional
    public ServerDTO enrollServer(ServerEnrollmentRequest request) {
        if (serverRepository.existsByServerName(request.getServerName())) {
            throw new RuntimeException("Server name already exists");
        }

        User currentUser = authService.getCurrentUser();

        // Encrypt API key
        String encryptedApiKey = encryptionService.encrypt(request.getApiKey());

        CognosServer server = CognosServer.builder()
                .serverName(request.getServerName())
                .baseUrl(request.getBaseUrl())
                .apiKeyEncrypted(encryptedApiKey)
                .prontoDebtorCode(request.getProntoDebtorCode())
                .prontoXiVersion(request.getProntoXiVersion())
                .enrolledBy(currentUser)
                .pollStatus(CognosServer.PollStatus.NEVER_POLLED)
                .isActive(true)
                .build();

        server = serverRepository.save(server);

        log.info("Server enrolled: {} by {}", server.getServerName(),
                currentUser != null ? currentUser.getUsername() : "unknown");

        auditService.logSuccess(currentUser, "SERVER_ENROLLED", "SERVER", server.getId(),
                null, null);

        return ServerDTO.fromEntity(server);
    }

    @Transactional
    public ServerDTO updateServer(Long id, ServerUpdateRequest request) {
        CognosServer server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        User currentUser = authService.getCurrentUser();

        // Update fields
        server.setServerName(request.getServerName());
        server.setBaseUrl(request.getBaseUrl());
        server.setProntoDebtorCode(request.getProntoDebtorCode());
        server.setProntoXiVersion(request.getProntoXiVersion());

        // Update API key only if provided
        if (request.getApiKey() != null && !request.getApiKey().isEmpty()) {
            String encryptedApiKey = encryptionService.encrypt(request.getApiKey());
            server.setApiKeyEncrypted(encryptedApiKey);
        }

        server = serverRepository.save(server);

        log.info("Server updated: {} by {}", server.getServerName(),
                currentUser != null ? currentUser.getUsername() : "unknown");

        auditService.logSuccess(currentUser, "SERVER_UPDATED", "SERVER", server.getId(),
                null, null);

        return ServerDTO.fromEntity(server);
    }

    @Transactional
    public void deleteServer(Long id) {
        CognosServer server = serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        User currentUser = authService.getCurrentUser();
        server.setIsActive(false);
        serverRepository.save(server);

        auditService.logSuccess(currentUser, "SERVER_DELETED", "SERVER", id, null, null);
    }

    public CognosServer getServerEntity(Long id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Server not found"));
    }
}
