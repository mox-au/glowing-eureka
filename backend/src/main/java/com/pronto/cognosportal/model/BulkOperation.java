package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_operations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;

    @Column(name = "operation_name")
    private String operationName;

    @Column(name = "target_servers", nullable = false, columnDefinition = "BIGINT[]")
    private Long[] targetServers;

    @Column(name = "content_path")
    private String contentPath;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OperationStatus status = OperationStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by")
    private User initiatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "success_count")
    private Integer successCount = 0;

    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    public enum OperationStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }

    public void incrementSuccessCount() {
        this.successCount = (this.successCount == null ? 0 : this.successCount) + 1;
    }

    public void incrementFailureCount() {
        this.failureCount = (this.failureCount == null ? 0 : this.failureCount) + 1;
    }
}
