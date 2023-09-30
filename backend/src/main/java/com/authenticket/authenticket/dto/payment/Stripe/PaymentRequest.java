package com.authenticket.authenticket.dto.payment.Stripe;

import lombok.Data;

@Data
public class PaymentRequest extends CustomerModel{
    public enum Currency{
        SGD,USD;
    }

    private String description;
    private int amount;
    private Currency currency;
    private String stripeEmail;
    private String token;
    private String type;
}
