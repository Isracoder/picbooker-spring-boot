package com.example.picbooker.payments;

import static java.util.Objects.isNull;

import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer.PhotographerService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.LoginLink;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.LoginLinkCreateOnAccountParams;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class StripeConnectService {

        @Value("${stripe.api.key}")
        private String stripeApiKey;

        @Value("${frontend.successUrl}") // e.g., https://yourapp.com/payment-success?sessionId={CHECKOUT_SESSION_ID}
        private String successUrl;

        @Value("${frontend.cancelUrl}") // e.g., https://yourapp.com/payment-cancel
        private String cancelUrl;

        @Autowired
        private PhotographerService photographerService;

        @PostConstruct
        public void init() {
                Stripe.apiKey = stripeApiKey;
        }

        // 2 versions of the 2 functions
        // these 2 not in use rn
        public String createConnectedAccount(String email) throws StripeException {
                AccountCreateParams params = AccountCreateParams.builder()
                                .setType(AccountCreateParams.Type.EXPRESS) // Use STANDARD for more control
                                .setEmail(email)
                                .setCapabilities(
                                                AccountCreateParams.Capabilities.builder()
                                                                .setCardPayments(
                                                                                AccountCreateParams.Capabilities.CardPayments
                                                                                                .builder()
                                                                                                .setRequested(true)
                                                                                                .build())
                                                                .setTransfers(
                                                                                AccountCreateParams.Capabilities.Transfers
                                                                                                .builder()
                                                                                                .setRequested(true)
                                                                                                .build())
                                                                .build())
                                .build();

                Account account = Account.create(params);
                return account.getId();
        }

        // these 2 in use
        @Transactional
        public String createStripeAccount(Photographer photographer) throws StripeException {

                AccountCreateParams params = AccountCreateParams.builder()
                                .setType(AccountCreateParams.Type.EXPRESS)
                                .setCountry("US") // Change based on region
                                .setEmail(photographer.getUser().getEmail())
                                .build();

                Account account = Account.create(params);
                photographer.setStripeAccountId(account.getId());

                // Save the photographer entity with the new Stripe account ID
                photographerService.save(photographer);

                return account.getId();
        }

        public String getOnboardingLink(String stripeAccountId) throws StripeException {

                AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                                .setAccount(stripeAccountId)
                                .setRefreshUrl("https://yourplatform.com/reauth") // Redirect if onboarding fails // to
                                                                                  // do change
                                .setReturnUrl("https://yourplatform.com/success") // Redirect after onboarding // to do
                                                                                  // change
                                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                                .build();

                AccountLink accountLink = AccountLink.create(params);
                return accountLink.getUrl();
        }

        public LoginLink getDashboardLink(Photographer photographer) throws StripeException {
                if (!isNull(photographer.getStripeAccountId()))
                        throw new ApiException(HttpStatus.BAD_REQUEST, "You haven't set up online payments yet.");
                // AccountSession session = AccountSession.create(
                // AccountSessionCreateParams.builder()
                // .setAccount(photographer.getStripeAccountId())
                // .build());
                LoginLinkCreateOnAccountParams params = LoginLinkCreateOnAccountParams.builder().build();

                LoginLink loginLink = LoginLink.createOnAccount("{{CONNECTED_ACCOUNT_ID}}", params,
                                RequestOptions.getDefault());
                // what is account dashboard session should be that instead of session
                // return url
                return loginLink;
        }

        public String getClientCheckoutLink(Long sessionId, Long photographerId, Long amountInCents, String currency)
                        throws StripeException {

                // Create the checkout session
                SessionCreateParams params = SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl(successUrl.replace("{CHECKOUT_SESSION_ID}", sessionId.toString()))
                                .setCancelUrl(cancelUrl)
                                .addLineItem(
                                                SessionCreateParams.LineItem.builder()
                                                                .setQuantity(1L)
                                                                .setPriceData(
                                                                                SessionCreateParams.LineItem.PriceData
                                                                                                .builder()
                                                                                                .setCurrency(currency)
                                                                                                .setUnitAmount(amountInCents)
                                                                                                .setProductData(
                                                                                                                SessionCreateParams.LineItem.PriceData.ProductData
                                                                                                                                .builder()
                                                                                                                                .setName("Photography Session Deposit")
                                                                                                                                .setDescription("Deposit for session ID: "
                                                                                                                                                + sessionId)
                                                                                                                                .build())
                                                                                                .build())
                                                                .build())
                                .setPaymentIntentData(
                                                SessionCreateParams.PaymentIntentData.builder()
                                                                .setApplicationFeeAmount(0L) // Optional: Platform fee
                                                                                             // if applicable
                                                                .setTransferData(
                                                                                SessionCreateParams.PaymentIntentData.TransferData
                                                                                                .builder()
                                                                                                .setDestination(getPhotographerStripeAccount(
                                                                                                                photographerId))
                                                                                                .build())
                                                                .build())
                                .build();

                Session session = Session.create(params);
                return session.getUrl(); // Return the checkout link
        }

        private String getPhotographerStripeAccount(Long photographerId) {
                // Fetch photographer's Stripe account ID from DB
                // Example: return
                // photographerRepository.findById(photographerId).get().getStripeAccountId();
                return "acct_xxxxxxx"; // Replace with actual lookup logic
        }

        public static long getSmallestAmountForCurrency(Double amount, Currency currency) {
                int fractionDigits = Currency.getInstance(currency.getCurrencyCode())
                                .getDefaultFractionDigits();
                int factor = (int) Math.pow(10, fractionDigits);
                long amountInSmallestUnit = Math.round(amount * factor); // Use Math.round to handle edge
                                                                         // cases
                return amountInSmallestUnit;
        }
}