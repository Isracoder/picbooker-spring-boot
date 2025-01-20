package com.example.picbooker.photographer_additionalService;

import java.io.Serializable;

import com.example.picbooker.additionalService.AdditionalService;
import com.example.picbooker.photographer.Photographer;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class PhotographerAdditionalService implements Serializable {

    @EmbeddedId
    private PhotographerAdditionalServiceId id;
    // photographer id , service id -> composite

    @ManyToOne
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @ManyToOne
    @JoinColumn(name = "additional_service", nullable = false)
    private AdditionalService adddAdditionalService;

    @Column
    private Boolean paymentRequired;

    @Column
    private Double fee;

    @Column
    private String currency;

    @Column
    private String details;

}
