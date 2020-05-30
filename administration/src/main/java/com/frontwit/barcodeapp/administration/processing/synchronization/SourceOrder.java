package com.frontwit.barcodeapp.administration.processing.synchronization;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SourceOrder {
    private Long id;
    private String nr;
    private String fronts;
    private Date orderedAt;
    private Date deadline;
    private String type;
    private BigDecimal valuation;
    private String additionalInfo;
    private String description;
    private String features;
    private String route;
    private Long customerId;
    private String customerName;
    private String customerAddress;
    private String phoneNumber;
}
