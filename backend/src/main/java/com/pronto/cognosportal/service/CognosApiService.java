package com.pronto.cognosportal.service;

import com.pronto.cognosportal.model.CognosServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognosApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EncryptionService encryptionService;

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
