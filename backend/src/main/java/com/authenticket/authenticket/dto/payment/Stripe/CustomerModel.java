package com.authenticket.authenticket.dto.payment.Stripe;

import com.authenticket.authenticket.dto.payment.Stripe.PaymentRequest.Currency;

import lombok.Data;

@Data
public class CustomerModel {
    public String name;

    public String description ;

    public String email;


    public int balance;

    public Currency currency;
}
