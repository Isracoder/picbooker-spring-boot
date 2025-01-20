package com.example.picbooker.additionalService;

import java.util.ArrayList;
import java.util.List;

import com.example.picbooker.photographer_additionalService.PhotographerAdditionalService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
public class AdditionalService {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Default
    @OneToMany(mappedBy = "photographer_additional_service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PhotographerAdditionalService> photographerAdditionalServices = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column
    private AdditionalServiceType type;

    @Column
    private String description;

}
