package com.jessica.transactions.client;

import com.jessica.transactions.mock.PaymentTransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PaymentApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<PaymentTransaction> getTransactions(String customerId){
        String url = "http://localhost:8080/mock/payment/transactions/" + customerId;
        return  Arrays.asList(restTemplate.getForObject(url, PaymentTransaction[].class));
    }
}
