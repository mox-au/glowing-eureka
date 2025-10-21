package com.pronto.cognosportal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BulkOperationResponse {
    private Long operationId;
    private String status;
    private Integer targetCount;
}
