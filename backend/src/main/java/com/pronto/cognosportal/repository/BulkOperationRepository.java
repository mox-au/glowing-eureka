package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.BulkOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkOperationRepository extends JpaRepository<BulkOperation, Long> {

    List<BulkOperation> findByStatus(BulkOperation.OperationStatus status);

    List<BulkOperation> findByInitiatedById(Long userId);

    List<BulkOperation> findByOrderByCreatedAtDesc();
}
