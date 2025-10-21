package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private CognosServer server;

    @Column(name = "report_count")
    private Integer reportCount = 0;

    @Column(name = "dashboard_count")
    private Integer dashboardCount = 0;

    @Column(name = "data_module_count")
    private Integer dataModuleCount = 0;

    @CreationTimestamp
    @Column(name = "captured_at", updatable = false)
    private LocalDateTime capturedAt;
}
