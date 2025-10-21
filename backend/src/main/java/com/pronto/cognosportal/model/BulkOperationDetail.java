package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bulk_operation_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkOperationDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulk_operation_id")
    private BulkOperation bulkOperation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private CognosServer server;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DetailStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "executed_at", updatable = false)
    private LocalDateTime executedAt;

    public enum DetailStatus {
        SUCCESS, FAILED, PENDING, IN_PROGRESS
    }
}
