package com.example.platform.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "coins")
public class Crypto {

    @Id
    @Column(name = "coins_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "coins_title")
    private String coinsTitle;
    @Column(name = "coins_code")
    private String coinsCode;
    @Column(name = "coins_tax")
    private String coinsTax;
    @Column(name = "status")
    private Boolean status = true;
    @ManyToMany(mappedBy = "cryptos")
    private Set<Platform> platforms = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "crypto_investor",
            joinColumns = @JoinColumn(name = "coins_id"),
            inverseJoinColumns = @JoinColumn(name = "investor_id")
    )
    @JsonManagedReference("investor_ref2")
    private Set<Investor> investors = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }


//    public List<PlatformCrypto> getPlatformCryptos() {
//        return platformCryptos;
//    }
//
//    public void setPlatformCryptos(List<PlatformCrypto> platformCryptos) {
//        this.platformCryptos = platformCryptos;
//    }

    public String getCoinsTitle() {
        return coinsTitle;
    }

    public void setCoinsTitle(String coinsTitle) {
        this.coinsTitle = coinsTitle;
    }

    public String getCoinsCode() {
        return coinsCode;
    }

    public void setCoinsCode(String coinsCode) {
        this.coinsCode = coinsCode;
    }

    public String getCoinsTax() {
        return coinsTax;
    }

    public void setCoinsTax(String coinsTax) {
        this.coinsTax = coinsTax;
    }

    public Set<Platform> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(Set<Platform> platforms) {
        this.platforms = platforms;
    }

    public Set<Investor> getInvestors() {
        return investors;
    }

    public void setInvestors(Set<Investor> investors) {
        this.investors = investors;
    }
}