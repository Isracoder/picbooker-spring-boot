package com.example.picbooker.payments;

import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.picbooker.ApiException;
import com.example.picbooker.session.SessionService;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserService;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @Autowired
    private StripeConnectService stripeConnectService;

    @Autowired
    private SessionService sessionService;

    // refactor upon testing
    @PostMapping("/webhook")
    public String handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            // Verify the webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);

            // Handle the event
            switch (event.getType()) {
                case "payment_intent.succeeded":
                    PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                            .orElseThrow();
                    handlePaymentIntentSucceeded(paymentIntent);
                    break;
                case "payment_intent.payment_failed":
                    PaymentIntent failedPaymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                            .orElseThrow();
                    handlePaymentIntentFailed(failedPaymentIntent);
                    break;
                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }

            return "Webhook processed successfully";
        } catch (Exception e) {
            System.err.println("Error processing webhook: " + e.getMessage());
            return "Webhook processing failed";
        }
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent) {
        // Extract metadata (e.g., session ID) from the payment intent
        String sessionId = paymentIntent.getMetadata().get("session_id");
        sessionService.confirmSessionDepositPayment(Long.parseLong(sessionId));
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent) {
        String sessionId = paymentIntent.getMetadata().get("session_id");
        sessionService.failSessionDepositPayment(Long.parseLong(sessionId));
    }

    @PostMapping("/onboard") // for setting up photographer details
    public String onboardPhotographer(@RequestParam String email) throws Exception {
        User user = UserService.getLoggedInUserThrow();
        if (user.getEmail() != email || !user.getIsEmailVerified() || !isNull(user.getPhotographer())) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Account not suitable for payment details to be added to it.");
        }
        String accountId = stripeConnectService.createConnectedAccount(email);
        String accountLinkUrl = stripeConnectService.createAccountLink(accountId);
        return accountLinkUrl; // Redirect the photographer to this URL
    }

}