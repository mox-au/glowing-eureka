package com.pronto.cognosportal.controller;

import com.pronto.cognosportal.dto.BulkDeployRequest;
import com.pronto.cognosportal.dto.BulkOperationResponse;
import com.pronto.cognosportal.model.BulkOperation;
import com.pronto.cognosportal.model.BulkOperationDetail;
import com.pronto.cognosportal.repository.BulkOperationDetailRepository;
import com.pronto.cognosportal.repository.BulkOperationRepository;
import com.pronto.cognosportal.service.BulkOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bulk-operations")
@RequiredArgsConstructor
public class BulkOperationController {

    private final BulkOperationService bulkOperationService;
    private final BulkOperationRepository bulkOperationRepository;
    private final BulkOperationDetailRepository detailRepository;

    @PostMapping("/deploy")
    public ResponseEntity<BulkOperationResponse> createBulkDeploy(@Valid @RequestBody BulkDeployRequest request) {
        BulkOperationResponse response = bulkOperationService.createBulkOperation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BulkOperation>> getAllOperations() {
        return ResponseEntity.ok(bulkOperationRepository.findByOrderByCreatedAtDesc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BulkOperation> getOperationById(@PathVariable Long id) {
        BulkOperation operation = bulkOperationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operation not found"));
        return ResponseEntity.ok(operation);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<List<BulkOperationDetail>> getOperationDetails(@PathVariable Long id) {
        return ResponseEntity.ok(detailRepository.findByBulkOperationId(id));
    }
}
