package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChangeHistoryRepository extends JpaRepository<ChangeHistory, Long> {

    List<ChangeHistory> findByServerId(Long serverId);

    List<ChangeHistory> findByServerIdAndChangedAtBetween(Long serverId, LocalDateTime startDate, LocalDateTime endDate);

    List<ChangeHistory> findByChangedById(Long userId);
}
