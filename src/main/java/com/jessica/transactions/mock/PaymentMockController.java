package com.jessica.transactions.mock;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/mock/payement")
public class PaymentMockController {

    @GetMapping("/transactions/{customerId}")
    public List<PaymentTransaction> getTransactions(@PathVariable String customerId){

        List<PaymentTransaction> transactions = new ArrayList<>();
        transactions.add(new PaymentTransaction("p1", "Apple Music", -50.00, "2026-02-18"));
                transactions.add(new PaymentTransaction("p2", "Bolt Ride", -40.00, "2026-02-19"));

                return transactions;
    }
}

