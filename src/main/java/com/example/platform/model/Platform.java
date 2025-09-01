
package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.*;

@Entity
public class Platform {

    @Id
    @Column(name = "platform_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Platform Title boş bırakılamaz.")
    @Column(name = "platform_title",nullable = false)
    private String platformTitle;
    @NotBlank(message = "Platform Code boş bırakılamaz.")
    @Column(name = "platform_code",nullable = false)
    private String platformCode;
    @NotBlank(message = "TaxNo boş bırakılamaz.")
    @Column(name = "tax_no",nullable = false)
    private String taxNo;
    @Column(name = "api_key", columnDefinition = "UUID")
    private UUID apiKey;
    @Column(name = "record_status")
    private String recordStatus = "A";

    private Boolean state;

    @OneToMany(mappedBy = "platform")  // investor sınıfındaki platform alanına referans
    @JsonManagedReference("investor_ref1")
    private List<Investor> investors;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "platform_crypto",
            joinColumns = @JoinColumn(name = "platform_id"),
            inverseJoinColumns = @JoinColumn(name = "coins_id")
    )
    @JsonManagedReference("crypto_ref1")
    private Set<Crypto> cryptos = new HashSet<>();


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

    public void setId(Long id) {
        this.id = id;
    }


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


    public List<Investor> getInvestors() {
        return investors;
    }

    public void setInvestors(List<Investor> investors) {
        this.investors = investors;
    }

    public String getRecord_status() {
        return recordStatus;
    }

    public void setRecord_status(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public Set<Crypto> getCryptos() {
        return cryptos;
    }

    public void setCryptos(Set<Crypto> cryptos) {
        this.cryptos = cryptos;
    }
}
