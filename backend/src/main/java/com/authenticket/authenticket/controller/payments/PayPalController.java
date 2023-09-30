package com.authenticket.authenticket.controller.payments;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.payment.PayPal.PaypalOrder;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.payments.PaypalServiceImpl;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/paypal")
public class PayPalController extends Utility {

    private final PaypalServiceImpl paypalService;

    public static final String SUCCESS_URL = "pay/success";//to replace with actual redirect
    public static final String CANCEL_URL = "pay/cancel";//to replace with actual redirect

    @Autowired
    public PayPalController(PaypalServiceImpl paypalService) {
        this.paypalService = paypalService;
    }

    @PostMapping("/pay")
    public ResponseEntity<GeneralApiResponse<Object>> pay(@RequestParam(value = "price") Double price,
                                                          @RequestParam(value = "currency") String currency,
                                                          @RequestParam(value = "method") String method,
                                                          @RequestParam(value = "intent") String intent,
                                                          @RequestParam(value = "description") String description){
        try {
            PaypalOrder order = PaypalOrder.builder()
                    .price(price)
                    .currency(currency)
                    .method(method)
                    .intent(intent)
                    .description(description)
                    .build();
            Payment payment = paypalService.createPayment(order.getPrice(), order.getCurrency(), order.getMethod(),
                    order.getIntent(), order.getDescription(), "http://localhost:3000/" + CANCEL_URL,
                    "http://localhost:3000/" + SUCCESS_URL);
            for(Links link:payment.getLinks()) {
                if(link.getRel().equals("approval_url")) {
                    return ResponseEntity.status(200).body(generateApiResponse(link.getHref(), "Payment successful"));
                }
            }

        } catch (PayPalRESTException e) {

            return ResponseEntity.status(406).body(generateApiResponse(null, e.getMessage()));
        }
        return ResponseEntity.status(406).body(generateApiResponse(null, "Payment failed"));
    }

    @GetMapping("/success-pay")
    public ResponseEntity<GeneralApiResponse<Object>> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                return ResponseEntity.status(200).body(generateApiResponse(null, "Payment successful"));
            }
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(406).body(generateApiResponse(null, e.getMessage()));
        }
        return ResponseEntity.status(406).body(generateApiResponse(null, "Payment failed"));
    }
}
