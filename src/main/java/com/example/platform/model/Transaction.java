package com.example.platform.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Column(name = "buyer_reg_no", nullable = false)
    private String buyerRegNo;

    @Column(name = "seller_reg_no", nullable = false)
    private String sellerRegNo;

    @ManyToOne
    @JoinColumn(name = "base_currency_id", nullable = false)
    private Crypto baseCurrency;

    @ManyToOne
    @JoinColumn(name = "quote_currency_id", nullable = false)
    private Crypto quoteCurrency;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal price;
}
