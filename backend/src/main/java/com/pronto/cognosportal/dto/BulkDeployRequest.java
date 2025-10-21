package com.pronto.cognosportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BulkDeployRequest {
    @NotBlank
    private String operationType;

    private String operationName;

    @NotEmpty
    private Long[] targetServers;

    private String contentPath;

    private String contentFile; // Base64 encoded content
}
