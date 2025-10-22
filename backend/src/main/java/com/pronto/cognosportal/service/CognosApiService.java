package com.pronto.cognosportal.service;

import com.pronto.cognosportal.model.CognosServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognosApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EncryptionService encryptionService;

    @Value("${cognos.api.demo-mode:true}")
    private boolean demoMode;

    /**
     * Test connectivity to Cognos server
     */
    public boolean testConnection(CognosServer server) {
        try {
            String apiKey = encryptionService.decrypt(server.getApiKeyEncrypted());
            HttpHeaders headers = createHeaders(apiKey);

            String url = server.getBaseUrl() + "/api/v1/configuration";
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.error("Failed to test connection to server {}: {}", server.getServerName(), e.getMessage());
            return false;
        }
    }

    /**
     * Get content inventory from Cognos server
     * NOTE: This is a placeholder implementation. Actual implementation should follow
     * IBM Cognos Analytics 12.0 REST API documentation
     */
    public Map<String, Object> getContentInventory(CognosServer server) {
        // Demo mode - return mock data for testing
        if (demoMode) {
            log.info("Demo mode: Returning mock data for server {}", server.getServerName());
            return getMockContentInventory();
        }

        // Real mode - attempt to connect to actual Cognos server
        try {
            String apiKey = encryptionService.decrypt(server.getApiKeyEncrypted());
            HttpHeaders headers = createHeaders(apiKey);

            // This endpoint is a placeholder - consult IBM Cognos Analytics 12.0 REST API docs
            String url = server.getBaseUrl() + "/api/v1/content";
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);

            return response.getBody();
        } catch (Exception e) {
            log.error("Failed to get content inventory from server {}: {}",
                    server.getServerName(), e.getMessage());
            throw new RuntimeException("Failed to fetch content inventory", e);
        }
    }

    /**
     * Returns mock data for demo/testing purposes
     */
    private Map<String, Object> getMockContentInventory() {
        Map<String, Object> inventory = new HashMap<>();

        // Mock reports
        List<Map<String, String>> reports = new ArrayList<>();
        reports.add(createMockContent("Sales Report Q4", "1.2.0", "/Reports/Sales"));
        reports.add(createMockContent("Financial Overview", "2.1.0", "/Reports/Finance"));
        reports.add(createMockContent("Customer Analytics", "1.0.5", "/Reports/Analytics"));

        // Mock dashboards
        List<Map<String, String>> dashboards = new ArrayList<>();
        dashboards.add(createMockContent("Executive Dashboard", "3.0.0", "/Dashboards/Executive"));
        dashboards.add(createMockContent("Operations Dashboard", "2.5.1", "/Dashboards/Ops"));

        // Mock data modules
        List<Map<String, String>> dataModules = new ArrayList<>();
        dataModules.add(createMockContent("Sales Data", "1.1.0", "/Data/Sales"));
        dataModules.add(createMockContent("Customer Data", "1.3.2", "/Data/Customer"));

        inventory.put("reports", reports);
        inventory.put("dashboards", dashboards);
        inventory.put("dataModules", dataModules);

        return inventory;
    }

    private Map<String, String> createMockContent(String name, String version, String path) {
        Map<String, String> content = new HashMap<>();
        content.put("name", name);
        content.put("version", version);
        content.put("path", path);
        return content;
    }

    /**
     * Deploy content to Cognos server
     * NOTE: This is a placeholder implementation
     */
    public void deployContent(CognosServer server, byte[] content, String path) {
        try {
            String apiKey = encryptionService.decrypt(server.getApiKeyEncrypted());
            HttpHeaders headers = createHeaders(apiKey);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // This endpoint is a placeholder - consult IBM Cognos Analytics 12.0 REST API docs
            String url = server.getBaseUrl() + "/api/v1/content/deploy?path=" + path;

            HttpEntity<byte[]> entity = new HttpEntity<>(content, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Deployment failed with status: " + response.getStatusCode());
            }

            log.info("Successfully deployed content to server {} at path {}",
                    server.getServerName(), path);
        } catch (Exception e) {
            log.error("Failed to deploy content to server {}: {}", server.getServerName(), e.getMessage());
            throw new RuntimeException("Failed to deploy content", e);
        }
    }

    private HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
