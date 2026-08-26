package com.flowermarket.dto;

import com.flowermarket.enums.WasteDestination;
import lombok.Data;

@Data
public class WasteRecordRequest {
    private Long flowerId;
    private Double quantity;
    private WasteDestination destination;
    private String notes;
}
