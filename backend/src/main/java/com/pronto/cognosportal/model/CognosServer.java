package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cognos_servers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CognosServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_name", unique = true, nullable = false, length = 100)
    private String serverName;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "api_key_encrypted", nullable = false, columnDefinition = "TEXT")
    private String apiKeyEncrypted;

    @Column(name = "pronto_debtor_code", nullable = false, length = 50)
    private String prontoDebtorCode;

    @Column(name = "pronto_xi_version", nullable = false, length = 20)
    private String prontoXiVersion;

    @CreationTimestamp
    @Column(name = "enrollment_date", updatable = false)
    private LocalDateTime enrollmentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrolled_by")
    private User enrolledBy;

    @Column(name = "last_poll_time")
    private LocalDateTime lastPollTime;

    @Column(name = "poll_status", length = 20)
    @Enumerated(EnumType.STRING)
    private PollStatus pollStatus = PollStatus.NEVER_POLLED;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    public enum PollStatus {
        SUCCESS, FAILED, IN_PROGRESS, NEVER_POLLED
    }
}
