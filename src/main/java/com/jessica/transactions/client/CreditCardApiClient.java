package com.jessica.transactions.client;

import com.jessica.transactions.mock.CreditCardTransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Component
public class CreditCardApiClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<CreditCardTransaction> getTransactions(String customerId){
        String url = "http://localhost:8080/mock/creditcard/transactions/" + customerId;

        CreditCardTransaction[] response =
                restTemplate.getForObject(url, CreditCardTransaction[].class);

        // convert array to list safely
        return response != null ? Arrays.asList(response) : new ArrayList<>();
    }
}
