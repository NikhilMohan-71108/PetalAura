package com.petalaura.library.dto;


import com.petalaura.library.enums.WalletTransactionHistory;
import com.petalaura.library.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletHistoryDto {
    private Long id;

    private double amount;

    private WalletTransactionHistory type;

    private String transactionStatus;

    private Wallet wallet;

    private String transactionId;

    private String paymentId;
}
