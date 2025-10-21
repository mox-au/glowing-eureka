package com.pronto.cognosportal.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "server_content_inventory")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private CognosServer server;

    @Column(name = "content_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "content_name", nullable = false)
    private String contentName;

    @Column(name = "content_version", length = 50)
    private String contentVersion;

    @Column(name = "content_path", nullable = false, columnDefinition = "TEXT")
    private String contentPath;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreationTimestamp
    @Column(name = "discovered_at", updatable = false)
    private LocalDateTime discoveredAt;

    public enum ContentType {
        REPORT, PACKAGE, DASHBOARD, DATA_MODULE
    }
}
