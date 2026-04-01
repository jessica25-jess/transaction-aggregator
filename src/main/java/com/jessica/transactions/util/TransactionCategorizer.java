package com.jessica.transactions.util;

import org.springframework.stereotype.Component;

@Component
public class TransactionCategorizer {

    public String categorize(String merchant){

        if (merchant == null || merchant.trim().isEmpty()){
            return "other";
        }

        merchant = merchant.toLowerCase();

        if(merchant.contains("uber") || merchant.contains("bolt"))
            return "transport";

        if(merchant.contains("checkers") || merchant.contains("woolworths"))
            return "groceries";

        if(merchant.contains("netflix") || merchant.contains("apple music"))
            return "entertainment";

        if(merchant.contains("salary"))
            return "income";

        return "other";
    }
}
