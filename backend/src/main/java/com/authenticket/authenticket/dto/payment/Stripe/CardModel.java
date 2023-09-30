package com.authenticket.authenticket.dto.payment.Stripe;

public class CardModel {
    private String number;
    private String expmonth;
    private String expyear;
    private String cvc ;


    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getExpmonth() {
        return expmonth;
    }
    public void setExpmonth(String expmonth) {
        this.expmonth = expmonth;
    }
    public String getExpyear() {
        return expyear;
    }
    public void setExpyear(String expyear) {
        this.expyear = expyear;
    }
    public String getCvc() {
        return cvc;
    }
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }
}
