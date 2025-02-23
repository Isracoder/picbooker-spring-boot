package com.example.picbooker.photographer_additionalService;

import java.util.Currency;

import com.example.picbooker.additionalService.AddOnType;
import com.example.picbooker.photographer.Photographer;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class PhotographerAddOn {
    // for example photographer 2 offers photoEditing for 10 NIS per 50 photos , no
    // upfront payment

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    // to think of enforcing uniqueness for p + type
    @Column
    private AddOnType type;

    @Column
    private String customSessionType;

    @Column
    private Boolean multipleAllowedInSession;

    @Column
    private String description;
    @Column
    private Double fee;
    // to think , add free Boolean ?

    @Default
    @Column
    private Currency currency = Currency.getInstance("USD");

}
