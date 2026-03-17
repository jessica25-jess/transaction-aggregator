package com.jessica.transactions.client;

import com.jessica.transactions.mock.BankTransaction;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Component
public class BankApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public List<BankTransaction> getTransactions(String customerId) {
        String url = "http://localhost:8080/mock/bank/transactions/" + customerId;

        BankTransaction[] response =
                restTemplate.getForObject(url, BankTransaction[].class);

        return response != null ? Arrays.asList(response) : new ArrayList<>();

    }
}
