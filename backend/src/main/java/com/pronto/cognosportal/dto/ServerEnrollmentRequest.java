package com.pronto.cognosportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServerEnrollmentRequest {
    @NotBlank
    private String serverName;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String apiKey;

    @NotBlank
    private String prontoDebtorCode;

    @NotBlank
    private String prontoXiVersion;
}
