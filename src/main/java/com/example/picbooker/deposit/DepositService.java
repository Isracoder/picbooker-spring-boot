package com.example.picbooker.deposit;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.picbooker.ApiException;
import com.example.picbooker.payments.StripeConnectService;
import com.example.picbooker.session.Session;
import com.example.picbooker.system_message.EmailService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class DepositService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private EmailService emailService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Autowired
    private DepositRepository depositRepository;

    public Deposit create(Session session, Double amount, Currency currency, LocalDateTime paidAt,
            DepositStatus depositStatus,
            PaymentMethod method, String stripePaymentIntentId) {

        return new Deposit(null, session, amount, currency, paidAt, stripePaymentIntentId, depositStatus, method);
    }

    public Deposit createAndSave(Session session, Double amount, Currency currency, LocalDateTime paidAt,
            DepositStatus depositStatus,
            PaymentMethod method, String stripePaymentIntentId) {
        return save(create(session, amount, currency, paidAt, depositStatus, method, stripePaymentIntentId));
    }

    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    public Optional<Deposit> findById(Long id) {
        return depositRepository.findById(id);
    }

    public Deposit findByIdThrow(Long id) {
        return depositRepository.findById(id).orElseThrow();
    }

    public PaymentIntent createPaymentIntent(Session session, long amount) throws StripeException {
        Deposit deposit = session.getDeposit();
        if (isNull(deposit) || deposit.getMethod() == PaymentMethod.CASH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Deposit not required or not requested in this method");
        }
        if (isNull(session.getPhotographer().getStripeAccountId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Photographer hasn't enabled online payment");
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                // to do set amount
                .setAmount(
                        StripeConnectService.getSmallestAmountForCurrency(deposit.getAmount(), deposit.getCurrency()))
                .setCurrency(deposit.getCurrency().getCurrencyCode().toLowerCase()) // expects lowercase
                .putMetadata("booking_id", session.getId().toString())
                .setTransferData(PaymentIntentCreateParams.TransferData.builder()
                        .setDestination(session.getPhotographer().getStripeAccountId())
                        .build())
                .setDescription("Deposit for booking ID: " + session.getId() + ", with deposit id: " + deposit.getId())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        deposit.setStripePaymentIntentId(paymentIntent.getId()); // to think of encrypting
        // return paymentIntent.getClientSecret();
        return paymentIntent;
    }

}
