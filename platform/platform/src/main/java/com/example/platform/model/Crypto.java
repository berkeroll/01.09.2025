package com.example.platform.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coins")
public class Crypto {

    @Id
    @Column(name = "coins_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coins_title")
    private String  coins_title;
    @Column(name = "coins_code")
    private String coins_code;
    @Column(name = "coins_tax")
    private String coins_tax;
    @Column (name = "status")
    private Boolean status=true;

    @OneToMany(mappedBy = "crypto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlatformCrypto> platformCryptos = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoins_title() {
        return coins_title;
    }

    public void setCoins_title(String coins_title) {
        this.coins_title = coins_title;
    }

    public String getCoins_code() {
        return coins_code;
    }

    public void setCoins_code(String coins_code) {
        this.coins_code = coins_code;
    }

    public String getCoins_tax() {
        return coins_tax;
    }

    public void setCoins_tax(String coins_tax) {
        this.coins_tax = coins_tax;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


    public List<PlatformCrypto> getPlatformCryptos() {
        return platformCryptos;
    }

    public void setPlatformCryptos(List<PlatformCrypto> platformCryptos) {
        this.platformCryptos = platformCryptos;
    }
}
