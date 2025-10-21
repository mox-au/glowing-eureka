package com.pronto.cognosportal.controller;

import com.pronto.cognosportal.dto.ServerDTO;
import com.pronto.cognosportal.dto.ServerEnrollmentRequest;
import com.pronto.cognosportal.service.PollingService;
import com.pronto.cognosportal.service.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverService;
    private final PollingService pollingService;

    @GetMapping
    public ResponseEntity<List<ServerDTO>> getAllServers(@RequestParam(required = false) Boolean active) {
        if (Boolean.TRUE.equals(active)) {
            return ResponseEntity.ok(serverService.getActiveServers());
        }
        return ResponseEntity.ok(serverService.getAllServers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getServerById(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServerDTO> enrollServer(@Valid @RequestBody ServerEnrollmentRequest request) {
        ServerDTO server = serverService.enrollServer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(server);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        serverService.deleteServer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/poll")
    public ResponseEntity<Map<String, String>> pollServer(@PathVariable Long id) {
        pollingService.pollServerAsync(id);
        return ResponseEntity.ok(Map.of("message", "Polling started", "serverId", id.toString()));
    }

    @PostMapping("/poll-all")
    public ResponseEntity<Map<String, String>> pollAllServers() {
        pollingService.scheduledPollAllServers();
        return ResponseEntity.ok(Map.of("message", "Polling all active servers"));
    }
}
