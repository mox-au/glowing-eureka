package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.ContentInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentInventoryRepository extends JpaRepository<ContentInventory, Long> {

    List<ContentInventory> findByServerId(Long serverId);

    List<ContentInventory> findByServerIdAndContentType(Long serverId, ContentInventory.ContentType contentType);

    @Modifying
    @Query("DELETE FROM ContentInventory ci WHERE ci.server.id = :serverId")
    void deleteByServerId(Long serverId);
}
