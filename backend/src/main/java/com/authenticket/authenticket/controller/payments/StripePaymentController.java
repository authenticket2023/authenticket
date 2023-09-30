package com.authenticket.authenticket.controller.payments;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.payment.Stripe.CardModel;
import com.authenticket.authenticket.dto.payment.Stripe.CustomerModel;
import com.authenticket.authenticket.dto.payment.Stripe.PaymentRequest;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.payments.StripeServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/payment/stripe")
public class StripePaymentController extends Utility {
    private Logger logger = Logger.getLogger(getClass().getName());
    private final StripeServiceImpl stripeService;

    @Autowired
    public StripePaymentController(StripeServiceImpl stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentRequest paymentInfo) throws StripeException {

        logger.info("paymentInfo.amount: "+paymentInfo.getAmount());
        PaymentIntent paymentIntent = stripeService.createPaymentIntent(paymentInfo);

        String paymentStr = paymentIntent.toJson();

        return new ResponseEntity<>(paymentStr, HttpStatus.OK);
    }

    //customer and card controller

    @PostMapping("/createCustomer")
    public ResponseEntity<String> createPayment(@RequestBody CustomerModel request){
        String paymentId = stripeService.createCustomer(request);
        return paymentId!=null? new ResponseEntity<String>(paymentId,HttpStatus.OK):
                new ResponseEntity<String>("Customer creation request failed ",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getCustomer")
    public ResponseEntity<String> getCustomedetails(@RequestParam String customerId){
        String paymentId = stripeService.getCustomer(customerId);
        return paymentId!=null? new ResponseEntity<String>(paymentId,HttpStatus.OK):
                new ResponseEntity<String>("Customer creation request failed ",HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/deleteCustomer")
    public ResponseEntity<String> deleteCustomer(@RequestParam String customerId){
        String paymentId = stripeService.deleteCustomer(customerId);
        return paymentId!=null? new ResponseEntity<String>(paymentId,HttpStatus.OK):
                new ResponseEntity<String>("Customer creation request failed ",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/addCardDetails")
    public ResponseEntity<String> addCardToCustomer(@RequestParam String customerId , @RequestBody CardModel request){
        Customer customer ;
        Card card;
        try {
            customer = Customer.retrieve(customerId); //add customer id here : it will start with cus_
            Map<String, Object> cardParam = new HashMap<String, Object>(); //add card details
            cardParam.put("number", request.getNumber());
            cardParam.put("exp_month", request.getExpmonth());
            cardParam.put("exp_year", request.getExpyear());
            cardParam.put("cvc", request.getCvc());

            Map<String, Object> tokenParam = new HashMap<String, Object>();
            tokenParam.put("card", cardParam);

            Token token = Token.create(tokenParam);

            Map<String, Object> source = new HashMap<String, Object>();
            source.put("source", token.getId());

            card = (Card)customer.getSources().create(source);
            String cardDetails = card.toJson();
            System.out.println("Card Details : " + cardDetails);
            customer = Customer.retrieve(customerId);//change the customer id or use to get customer by id
            System.out.println("After adding card, customer details : " + customer);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }
    //payment

    @PostMapping
    public ResponseEntity<String> completePayment(@RequestBody PaymentRequest request) throws StripeException {
        String chargeId= stripeService.charge(request);
        return chargeId!=null? new ResponseEntity<String>(chargeId,HttpStatus.OK):
                new ResponseEntity<String>("Please check the credit card details entered",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public String handleError(StripeException ex) {
        return ex.getMessage();
    }

    @PostMapping("/createPayment")
    public ResponseEntity<String> createPayment(@RequestBody PaymentRequest request){
        String paymentId = stripeService.createPayment(request);
        return paymentId!=null? new ResponseEntity<String>(paymentId,HttpStatus.OK):
                new ResponseEntity<String>("Please check the credit card details entered",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/capturePayment")
    public ResponseEntity<String> capturePayment(@RequestParam(name = "paymentId")String paymentId,
                                                 @RequestParam(name = "amount")Long amount){
        String response = stripeService.capturePayment(paymentId, amount);
        return response!=null? new ResponseEntity<String>(response,HttpStatus.OK):
                new ResponseEntity<String>("Please check the credit card details entered",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/confirmPayment")
    public ResponseEntity<String> confirmPayment(@RequestParam(name = "paymentId")String paymentId){
        String response = stripeService.confirmPayment(paymentId);
        return response!=null? new ResponseEntity<String>(response,HttpStatus.OK):
                new ResponseEntity<String>("Please check the credit card details entered",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/cancelPayment")
    public ResponseEntity<String> cancelPayment(@RequestParam(name = "paymentId")String paymentId){
        String response = stripeService.cancelPayment(paymentId);
        return response!=null? new ResponseEntity<String>(response,HttpStatus.OK):
                new ResponseEntity<String>("Please check the credit card details entered",HttpStatus.BAD_REQUEST);
    }
}
