package com.example.platform.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
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
    @JoinColumn(name = "platform_id",referencedColumnName = "platform_id",nullable = true)  // investor tablosundaki foreign key sütunu
    @JsonBackReference
    private Platform platform;








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
}
