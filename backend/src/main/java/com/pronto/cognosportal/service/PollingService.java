package com.pronto.cognosportal.service;

import com.pronto.cognosportal.model.*;
import com.pronto.cognosportal.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PollingService {

    private final CognosServerRepository serverRepository;
    private final ServerMetadataRepository metadataRepository;
    private final ContentInventoryRepository contentInventoryRepository;
    private final ChangeHistoryRepository changeHistoryRepository;
    private final CognosApiService cognosApiService;

    @Scheduled(cron = "${polling.schedule.cron}")
    public void scheduledPollAllServers() {
        log.info("Starting scheduled polling of all servers");
        List<CognosServer> servers = serverRepository.findByIsActiveTrue();
        log.info("Found {} active servers to poll", servers.size());

        for (CognosServer server : servers) {
            pollServerAsync(server.getId());
        }
    }

    @Async
    public void pollServerAsync(Long serverId) {
        CognosServer server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));
        pollServer(server);
    }

    @Transactional
    public void pollServer(CognosServer server) {
        log.info("Polling server: {}", server.getServerName());

        try {
            // Update status to IN_PROGRESS
            server.setPollStatus(CognosServer.PollStatus.IN_PROGRESS);
            server = serverRepository.save(server);

            // Call Cognos API to get content
            Map<String, Object> response = cognosApiService.getContentInventory(server);

            // Update server_metadata
            ServerMetadata metadata = ServerMetadata.builder()
                    .server(server)
                    .reportCount(getCountFromResponse(response, "reports"))
                    .dashboardCount(getCountFromResponse(response, "dashboards"))
                    .dataModuleCount(getCountFromResponse(response, "dataModules"))
                    .build();
            metadataRepository.save(metadata);

            // Update content_inventory (simplified - actual implementation would parse response properly)
            // This is a placeholder - real implementation would iterate through actual content items
            updateContentInventory(server, response);

            // Update server status
            server.setPollStatus(CognosServer.PollStatus.SUCCESS);
            server.setLastPollTime(LocalDateTime.now());
            server.setLastError(null);
            serverRepository.save(server);

            // Log to change_history
            logPollingEvent(server, "SUCCESS");

            log.info("Successfully polled server: {}", server.getServerName());
        } catch (Exception e) {
            log.error("Failed to poll server {}: {}", server.getServerName(), e.getMessage());

            server.setPollStatus(CognosServer.PollStatus.FAILED);
            server.setLastError(e.getMessage());
            serverRepository.save(server);

            logPollingEvent(server, "FAILED");
        }
    }

    private Integer getCountFromResponse(Map<String, Object> response, String key) {
        // Placeholder implementation
        if (response != null && response.containsKey(key)) {
            Object value = response.get(key);
            if (value instanceof List) {
                return ((List<?>) value).size();
            }
        }
        return 0;
    }

    @Transactional
    private void updateContentInventory(CognosServer server, Map<String, Object> response) {
        // Clear existing inventory for this server
        contentInventoryRepository.deleteByServerId(server.getId());

        // This is a placeholder - actual implementation would parse the response
        // and create ContentInventory entries based on the actual Cognos API response structure
        log.debug("Content inventory updated for server: {}", server.getServerName());
    }

    @Transactional
    private void logPollingEvent(CognosServer server, String status) {
        Map<String, Object> details = new HashMap<>();
        details.put("status", status);
        details.put("pollTime", LocalDateTime.now().toString());

        ChangeHistory changeHistory = ChangeHistory.builder()
                .server(server)
                .changeType("POLLING_" + status)
                .changeDetails(details)
                .initiatedFrom(ChangeHistory.InitiatedFrom.SCHEDULED)
                .build();

        changeHistoryRepository.save(changeHistory);
    }
}
