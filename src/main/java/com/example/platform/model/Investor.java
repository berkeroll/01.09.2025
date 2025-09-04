package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "investor", uniqueConstraints = @UniqueConstraint(columnNames = {"mernis", "platform_id"}))
public class Investor {

    @Id
    @Column(name = "object_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;


    @NotBlank(message = "TC Kimlik numarası boş olamaz")
    @Pattern(regexp = "^[0-9]{11}$", message = "TC Kimlik numarası 11 haneli ve sadece rakamlardan oluşmalıdır")
    private String mernis;

    @ManyToOne
    @JoinColumn(name = "platform_id", referencedColumnName = "platform_id", nullable = true)
    // investor tablosundaki foreign key sütunu
    @JsonBackReference("investor_ref1")
    private Platform platform;


    @ManyToMany(mappedBy = "investors")
    @JsonBackReference("investor_ref2")
    private Set<Crypto> cryptos = new HashSet<>();

    @Column(name = "sicil_no", unique = false, length = 8)
    private String sicilNo;
    @Column(name = "state")
    private String state = "A";

    @Column(name = "platformName")
    private String platformName;
    @Column(name = "platformStatus")
    private String status = "Aktif";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMernis() {
        return mernis;
    }

    public void setMernis(String mernis) {
        this.mernis = mernis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getSicilNo() {
        return sicilNo;
    }

    public void setSicilNo(String sicilNo) {
        this.sicilNo = sicilNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Crypto> getCryptos() {
        return cryptos;
    }

    public void setCryptos(Set<Crypto> cryptos) {
        this.cryptos = cryptos;
    }
}
