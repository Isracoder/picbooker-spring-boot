package com.example.picbooker.payments;

import static java.util.Objects.isNull;

import java.util.Currency;
import java.util.UUID;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.picbooker.ApiException;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.deposit.PaymentMethod;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.session.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;

@Service
public class PaymentProcessingService {

    private String pagePrefix = "https://pay.lahza.io/";

    private String testKey = "sk_test_hYszvCAG7yH3YalbNSSQpDpESDm3pF1pm";

    public String createPaymentPageForDeposit(Deposit deposit, Session session, Photographer photographer) {
        if (isNull(deposit) || deposit.getMethod() == PaymentMethod.CASH) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Deposit not required or not requested in this method");
        }
        if (isNull(photographer.getEnabledOnlinePayment())
                || photographer.getEnabledOnlinePayment() == false) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Photographer hasn't enabled online payment");
        }

        String smallestAmount = String
                .valueOf(PaymentProcessingService.getSmallestAmountForCurrency(deposit.getAmount(),
                        deposit.getCurrency()));
        String slug = "picbooker/" + UUID.randomUUID().toString().substring(0, 12);
        String description = "Deposit payment page for session on date:" + session.getDate().toString()
                + ".\nPhotographer: " + photographer.getPersonalName();

        JsonNode response = postToLahza(slug, description, smallestAmount, deposit.getCurrency().getCurrencyCode());
        if (isNull(response)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Couldn't create payment page: ");
        }
        return pagePrefix + slug;
    }

    public JsonNode postToLahza(String slug, String description, String smallestAmount, String currencyCode) {
        String tokenEndpoint = "https://api.lahza.io/page";

        // Create the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("slug", slug);
        requestBody.put("description", description);
        requestBody.put("name", "Picbooker deposit payment");
        requestBody.put("amount", smallestAmount);
        requestBody.put("currency", currencyCode);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + testKey);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                System.out.println(responseBody);
                return responseBody;
            } else
                return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Couldn't create payment page: " + e.getLocalizedMessage());
        }
    }

    public static long getSmallestAmountForCurrency(Double amount, Currency currency) {
        int fractionDigits = Currency.getInstance(currency.getCurrencyCode())
                .getDefaultFractionDigits();
        int factor = (int) Math.pow(10, fractionDigits);
        long amountInSmallestUnit = Math.round(amount * factor);
        return amountInSmallestUnit;
    }
}