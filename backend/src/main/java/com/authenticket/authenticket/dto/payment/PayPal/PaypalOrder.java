package com.authenticket.authenticket.dto.payment.PayPal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaypalOrder {
    private Double price;
    private String currency;
    private String method;
    private String intent;
    private String description;
}
