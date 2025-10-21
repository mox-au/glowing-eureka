package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.BulkOperationDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkOperationDetailRepository extends JpaRepository<BulkOperationDetail, Long> {

    List<BulkOperationDetail> findByBulkOperationId(Long bulkOperationId);

    List<BulkOperationDetail> findByServerId(Long serverId);
}
