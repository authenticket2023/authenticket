package com.authenticket.authenticket.dto.payment.Stripe;

import lombok.Data;

@Data
public class CardModel {
    private String number;
    private String expmonth;
    private String expyear;
    private String cvc ;

}
