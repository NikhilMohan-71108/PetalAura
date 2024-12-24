package com.petalaura.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String orderStatus;
    private String returnMessage;

    // These fields can be set to null if not needed for this specific query.
    private String pdfReport;
    private String csvReport;

    // You can also provide a constructor specifically for the fields that are being set by the query
    public OrderDto(String orderStatus, String returnMessage) {
        this.orderStatus = orderStatus;
        this.returnMessage = returnMessage;
        this.pdfReport = null; // Or set default values if needed
        this.csvReport = null; // Or set default values if needed
    }
}
