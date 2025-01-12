package com.example.picbooker.photographer_additionalService;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class PhotographerAdditionalService {

    // photographer id , service id -> composite

    @Column
    private Boolean paymentRequired;

    @Column
    private Double fee;

    @Column
    private String currency;

    @Column
    private String details;

}
