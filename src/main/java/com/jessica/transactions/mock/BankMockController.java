package com.jessica.transactions.mock;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping ("/mock/bank")
public class BankMockController {

    @GetMapping("/transactions/{customerId}")
    public List<BankTransaction> getTransactions(@PathVariable String customerId){

        List<BankTransaction> transactions =new ArrayList<>();
        transactions.add(new BankTransaction("b1","Woolworths", -120.50, "2026-02-10"));
                transactions.add(new BankTransaction("b2", "Uber", -80.00, "2026-02-11"));
                transactions.add(new BankTransaction("b2", "Salary Payment", 15000.00, "2026-02-01"));

                return transactions;
    }
}

