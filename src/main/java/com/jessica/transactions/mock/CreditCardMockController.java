package com.jessica.transactions.mock;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/mock/creditcard")
public class CreditCardMockController {

    @GetMapping("/transactions/{customerId}")
    public List<CreditCardTransaction> getTransactions(@PathVariable String customerId){

        List<CreditCardTransaction> transactions = new ArrayList<>();
        transactions.add(new CreditCardTransaction("c1", "Netflix",-55.00, "2026-02-12"));
                transactions.add(new CreditCardTransaction("c2", "Checkers", -300.00, "2026-02-16"));

                return transactions;
    }
}
