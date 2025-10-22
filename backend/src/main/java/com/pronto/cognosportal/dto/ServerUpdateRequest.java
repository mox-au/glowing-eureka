package com.pronto.cognosportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServerUpdateRequest {
    @NotBlank
    private String serverName;

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String prontoDebtorCode;

    @NotBlank
    private String prontoXiVersion;

    // API key is optional - only update if provided
    private String apiKey;
}
