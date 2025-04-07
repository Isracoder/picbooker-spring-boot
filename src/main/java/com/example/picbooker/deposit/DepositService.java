package com.example.picbooker.deposit;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// import com.example.picbooker.payments.StripeConnectService;
import com.example.picbooker.session.Session;

@Service
public class DepositService {

    // @Value("${stripe.api.key}")
    // private String stripeApiKey;

    // private final String stripeApiKey;

    // public DepositService() {
    // Dotenv dotenv = Dotenv.load();
    // this.stripeApiKey = dotenv.get("STRIPE_API_KEY");
    // }

    // @PostConstruct
    // public void init() {
    // Stripe.apiKey = stripeApiKey;
    // }

    // @Autowired
    // private EmailService emailService;

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

}
