package com.pronto.cognosportal.dto;

import com.pronto.cognosportal.model.CognosServer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerDTO {
    private Long id;
    private String serverName;
    private String baseUrl;
    private String prontoDebtorCode;
    private String prontoXiVersion;
    private LocalDateTime enrollmentDate;
    private String enrolledBy;
    private LocalDateTime lastPollTime;
    private String pollStatus;
    private Boolean isActive;
    private String lastError;

    public static ServerDTO fromEntity(CognosServer server) {
        return ServerDTO.builder()
                .id(server.getId())
                .serverName(server.getServerName())
                .baseUrl(server.getBaseUrl())
                .prontoDebtorCode(server.getProntoDebtorCode())
                .prontoXiVersion(server.getProntoXiVersion())
                .enrollmentDate(server.getEnrollmentDate())
                .enrolledBy(server.getEnrolledBy() != null ? server.getEnrolledBy().getUsername() : null)
                .lastPollTime(server.getLastPollTime())
                .pollStatus(server.getPollStatus() != null ? server.getPollStatus().name() : null)
                .isActive(server.getIsActive())
                .lastError(server.getLastError())
                .build();
    }
}
