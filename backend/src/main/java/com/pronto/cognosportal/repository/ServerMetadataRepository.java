package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.ServerMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServerMetadataRepository extends JpaRepository<ServerMetadata, Long> {

    @Query("SELECT sm FROM ServerMetadata sm WHERE sm.server.id = :serverId ORDER BY sm.capturedAt DESC LIMIT 1")
    Optional<ServerMetadata> findLatestByServerId(Long serverId);
}
