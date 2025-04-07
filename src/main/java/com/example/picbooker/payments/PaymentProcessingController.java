package com.example.picbooker.payments;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiResponse;
import com.example.picbooker.session.SessionService;

@RestController
@RequestMapping("/api/payments")
public class PaymentProcessingController {

    @Autowired
    private PaymentProcessingService paymentProcessingService;

    @Autowired
    private SessionService sessionService;

    // refactor upon testing
    @PostMapping("/webhook")
    public ApiResponse<Map<String, String>> handleStripeWebhook(@RequestBody String payload) {
        try {
            String event = "to do implement";
            System.out.println("In web hook");
            System.out.println(payload);
            // const hash = crypto.createHmac('sha256',
            // secret).update(req.body).digest('hex');
            // if (hash === req.headers['x-lahza-signature']) {
            // Retrieve the request's body

            // Handle the event
            switch (event) {

                case "charge.success":
                    System.out.println("Payment intent succeeded webhook triggered");

                    break;

                default:
                    System.out.println("Unhandled event type: " + event);
            }

            return ApiResponse.<Map<String, String>>builder().content(Map.of("status", "success"))
                    .status(HttpStatus.OK).build();
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return ApiResponse.<Map<String, String>>builder().content(Map.of("status", "failed"))
                    .status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/webhook")
    public ApiResponse<Map<String, String>> callback() {
        return ApiResponse.<Map<String, String>>builder().content(Map.of("status", "success"))
                .status(HttpStatus.OK).build();

    }

}