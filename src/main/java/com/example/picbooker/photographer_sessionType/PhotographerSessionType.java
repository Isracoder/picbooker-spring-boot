package com.example.picbooker.photographer_sessionType;

import java.util.Currency;

import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionTypeName;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table
@Builder
public class PhotographerSessionType {

    // Default no-args constructor
    public PhotographerSessionType() {
        this.currency = Currency.getInstance("USD");
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @Column
    private SessionTypeName type;

    @Column
    private String customSessionType;

    @Column
    private Double pricePerDuration;

    @Column
    private String description;

    @Column
    private String location;

    // info about deposit
    // required/not , if so amount
    @Column
    private Boolean isPrivate;

    @Column
    private Boolean requiresDeposit;

    @Column
    private Double depositAmount; // same currency as other

    @Column
    @Default
    private Integer durationMinutes = 60;

    @Column
    @Builder.Default
    private Currency currency = Currency.getInstance("USD");

    @PrePersist
    public void prePersist() {
        if (this.currency == null) {
            this.currency = (Currency.getInstance("USD"));
        }
    }

    public Currency getCurrency() {
        return currency != null ? currency : Currency.getInstance("USD");
    }

    public void setCurrency(Currency currency) {
        if (currency != null)
            this.currency = currency;
        else
            this.currency = Currency.getInstance("USD");
    }

    // may have photos that I can add

}
