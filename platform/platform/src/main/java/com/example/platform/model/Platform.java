package com.example.platform.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class Platform {

    @Id
    @Column(name = "platform_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platformTitle;
    private String platformCode;
    private String taxNo;
    @Column(name = "api_key", columnDefinition = "UUID")
    private UUID apiKey;

    private Boolean state;

    @OneToMany(mappedBy = "platform")  // investor sınıfındaki platform alanına referans
    @JsonManagedReference
    private List<Investor> investors;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlatformCrypto> platformCryptos = new ArrayList<>();




    public String getPlatformTitle() {
        return platformTitle;
    }

    public void setPlatformTitle(String platformTitle) {
        this.platformTitle = platformTitle;
    }

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }



    public Long getId() {
        return id;
    }

    public void setId(  Long id) {
        this.id = id;
    }



//    public List<Investor> getInvestors() {
//        return investors;
//    }
//
//    public void setInvestors(List<Investor> investors) {
//        this.investors = investors;
//    }

    public UUID getApiKey() {
        return apiKey;
    }

    public void setApiKey(UUID apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public List<PlatformCrypto> getPlatformCryptos() {
        return platformCryptos;
    }

    public void setPlatformCryptos(List<PlatformCrypto> platformCryptos) {
        this.platformCryptos = platformCryptos;
    }
}
