package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "change_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private CognosServer server;

    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "change_details", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> changeDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private User changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false)
    private LocalDateTime changedAt;

    @Column(name = "initiated_from", length = 20)
    @Enumerated(EnumType.STRING)
    private InitiatedFrom initiatedFrom = InitiatedFrom.PORTAL;

    public enum InitiatedFrom {
        PORTAL, API, SCHEDULED
    }
}
