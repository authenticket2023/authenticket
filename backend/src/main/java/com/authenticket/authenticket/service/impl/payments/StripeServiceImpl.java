package com.authenticket.authenticket.service.impl.payments;

import com.authenticket.authenticket.dto.payment.Stripe.CardModel;
import com.authenticket.authenticket.dto.payment.Stripe.CustomerModel;
import com.authenticket.authenticket.dto.payment.Stripe.PaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StripeServiceImpl {

//    public StripeToken createCardToken(StripeToken model) {
//
//        try {
//            Map<String, Object> card = new HashMap<>();
//            card.put("number", model.getCardNumber());
//            card.put("exp_month", Integer.parseInt(model.getExpMonth()));
//            card.put("exp_year", Integer.parseInt(model.getExpYear()));
//            card.put("cvc", model.getCvc());
//            Map<String, Object> params = new HashMap<>();
//            params.put("card", card);
//            Token token = Token.create(params);
//            if (token != null && token.getId() != null) {
//                model.setSuccess(true);
//                model.setToken(token.getId());
//            }
//            return model;
//        } catch (StripeException e) {
//            log.error("StripeService (createCardToken)", e);
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }
//
//    public StripeCharge charge(StripeCharge chargeRequest) {
//
//
//        try {
//            chargeRequest.setSuccess(false);
//            Map<String, Object> chargeParams = new HashMap<>();
//            chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
//            chargeParams.put("currency", "USD");
//            chargeParams.put("description", "Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
//            chargeParams.put("source", chargeRequest.getStripeToken());
//            Map<String, Object> metaData = new HashMap<>();
//            metaData.put("id", chargeRequest.getChargeId());
//            metaData.putAll(chargeRequest.getAdditionalInfo());
//            chargeParams.put("metadata", metaData);
//            Charge charge = Charge.create(chargeParams);
//            chargeRequest.setMessage(charge.getOutcome().getSellerMessage());
//
//            if (charge.getPaid()) {
//                chargeRequest.setChargeId(charge.getId());
//                chargeRequest.setSuccess(true);
//
//            }
//            return chargeRequest;
//        } catch (StripeException e) {
//            log.error("StripeService (charge)", e);
//            throw new RuntimeException(e.getMessage());
//        }
//
//    }

    public PaymentIntent createPaymentIntent(PaymentRequest paymentInfo) throws StripeException {

        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");


        System.out.println("paymentInfo.getReceiptEmail() :"+paymentInfo.getStripeEmail());
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfo.getAmount());
        params.put("currency", paymentInfo.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);
        params.put("description", "Authenticket Ticket Purchase");
        params.put("receipt_email", paymentInfo.getStripeEmail());

        return PaymentIntent.create(params);
    }

    //customer and card

    public String createCustomer(CustomerModel request) {
        Map<String, Object> params = new HashMap();
        params.put("name",request.getName());
        params.put("email", request.getEmail());
        params.put("description",request.getDescription());
        params.put("balance",request.getBalance());
        //params.put("currency",PaymentRequest.Currency.USD.name());

        try {
            Customer customer = Customer.create(params);
            System.out.println(customer);
            return customer.getId();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCustomer(String customerId) {
        try {
            Customer customer = Customer.retrieve(customerId);
            System.out.println(customer);
            return customer.getId();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteCustomer(String customerId) {
        Customer customer;
        try {
            customer = Customer.retrieve(customerId);
            Customer deletedCustomer = customer.delete();
            System.out.println(deletedCustomer);
            return customer.getId();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addCardDetails(CardModel cardetails , String customerId) {
        Customer customer ;
        Card card;
        try {
            customer = Customer.retrieve(customerId); //add customer id here : it will start with cus_
            Map<String, Object> cardParam = new HashMap<String, Object>(); //add card details
            cardParam.put("number", cardetails.getNumber());
            cardParam.put("exp_month", cardetails.getExpmonth());
            cardParam.put("exp_year", cardetails.getExpyear());
            cardParam.put("cvc", cardetails.getCvc());

            Map<String, Object> tokenParam = new HashMap<String, Object>();
            tokenParam.put("card", cardParam);

            Token token = Token.create(tokenParam); // create a token

            Map<String, Object> source = new HashMap<String, Object>();
            source.put("source", token.getId()); //add token as source
            card = (Card)customer.getSources().create(source);
            String cardDetails = card.toJson();
            System.out.println("Card Details : " + cardDetails);
            customer = Customer.retrieve(customerId);
            System.out.println("After adding card, customer details : " + customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;


    }
    //payment

    public String charge(PaymentRequest chargeRequest) throws StripeException {
        Map<String, Object> chargeParams = new HashMap();
        chargeParams.put("amount", chargeRequest.getAmount());
        chargeParams.put("currency", PaymentRequest.Currency.SGD);
        chargeParams.put("source", chargeRequest.getToken());

        Charge charge = Charge.create(chargeParams);
        System.out.println(charge);
        System.out.println("..................");
        System.out.println(charge.toString());
        return charge.getId();
    }

    public String createPayment(PaymentRequest paymentRequest) {
        PaymentIntent response ;
        try {
            PaymentIntentCreateParams params;
            if(paymentRequest.getType().equals("bid")) {
                params =  PaymentIntentCreateParams.builder()
                        .setAmount((long) paymentRequest.getAmount())
                        .setCurrency(PaymentRequest.Currency.USD.name())
                        .addPaymentMethodType("card")
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .build();
                response = PaymentIntent.create(params);
                return response.getId();

            }else if(paymentRequest.getType().equals("direct")){
                params =  PaymentIntentCreateParams.builder()
                        .setAmount((long) paymentRequest.getAmount())
                        .setCurrency(PaymentRequest.Currency.USD.name())
                        .addPaymentMethodType("card")
//					    .setAutomaticPaymentMethods(
//					    	      PaymentIntentCreateParams.CaptureMethod
//					    	        .builder()
//					    	        .setEnabled(true)
//					    	        .build()
//					    	    )
                        .build();
                response = PaymentIntent.create(params);
                System.out.println(response.getId());
                confirmPayment(response.getId());
                return response.getId();

            }
            else if(paymentRequest.getType().equals("captured")){
                params =  PaymentIntentCreateParams.builder()
                        .setAmount((long) paymentRequest.getAmount())
                        .setCurrency(PaymentRequest.Currency.USD.name())
                        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                        .build();
                response = PaymentIntent.create(params);
                System.out.println(response);
                System.out.println(response.getId());
                confirmPayment(response.getId());
                //capturePayment(response.getId() , Long.valueOf(paymentRequest.getAmount()));
                return response.getId();

            }

        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String confirmPayment(String paymentId) {
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(paymentId);
            Map<String, Object> params = new HashMap();
            params.put("payment_method", "pm_card_visa");

            PaymentIntent updatedPaymentIntent = paymentIntent.confirm(params);
            System.out.println(updatedPaymentIntent);
            System.out.println("...........................");
            System.out.println(updatedPaymentIntent.toString());
            return updatedPaymentIntent.getStatus();

        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String capturePayment(String id , Long amount) {
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(id);
            paymentIntent.setAmountCapturable(amount);
            PaymentIntent updatedPaymentIntent = paymentIntent.capture();
            System.out.println(updatedPaymentIntent);
            System.out.println("...........................");
            System.out.println(updatedPaymentIntent.toString());
            return updatedPaymentIntent.getStatus();
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;

    }

    public String cancelPayment(String id) {
        PaymentIntent paymentIntent;
        try {
            paymentIntent = PaymentIntent.retrieve(id );
            PaymentIntent updatedPaymentIntent = paymentIntent.cancel();
            System.out.println(updatedPaymentIntent);
            return updatedPaymentIntent.getStatus();
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return null;

    }

//    public PaymentIntent requestPaymentIntentWithoutCustomer(String firstName, String lastName, String email, String phone,
//                                                             OrderAddressCriteria shippingAddress, OrderAddressCriteria billingAddress, BigDecimal amount) throws StripeException;

}
