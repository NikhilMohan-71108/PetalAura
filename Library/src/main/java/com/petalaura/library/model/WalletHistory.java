package com.petalaura.library.model;

import com.petalaura.library.enums.WalletTransactionHistory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@Table(name="wallet_history")
public class WalletHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    private double amount;
    private WalletTransactionHistory type;
    private String transactionStatus;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="wallet_id",referencedColumnName = "wallet_id")
    private Wallet wallet;
}
