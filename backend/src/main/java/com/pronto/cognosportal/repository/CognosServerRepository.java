package com.pronto.cognosportal.repository;

import com.pronto.cognosportal.model.CognosServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CognosServerRepository extends JpaRepository<CognosServer, Long> {

    Optional<CognosServer> findByServerName(String serverName);

    List<CognosServer> findByIsActiveTrue();

    List<CognosServer> findByProntoDebtorCode(String debtorCode);

    List<CognosServer> findByProntoXiVersion(String xiVersion);

    boolean existsByServerName(String serverName);
}
