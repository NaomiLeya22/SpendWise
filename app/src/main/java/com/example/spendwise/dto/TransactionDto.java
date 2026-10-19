package com.example.spendwise.dto;

public class TransactionDto {

    public long id;
    public String clientUuid;
    public String type;
    public String category;
    public double amount;
    public String description;
    public String transactionDate;

    public TransactionDto() {
    }

    public TransactionDto(
            String clientUuid,
            String type,
            String category,
            double amount,
            String description,
            String transactionDate
    ) {
        this.clientUuid = clientUuid;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}
