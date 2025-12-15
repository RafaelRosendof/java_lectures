package com.ms2.ms2.Communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO{
    private String sagaId;

    private String status;

    private String collectedData;

    private String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }
}