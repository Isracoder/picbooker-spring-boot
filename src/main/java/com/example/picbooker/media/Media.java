package com.example.picbooker.media;

import com.example.picbooker.photographer.Photographer;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Table // to think of unique constraint on photographer + profile pic
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "photographer_id", nullable = false)
    private Photographer photographer;

    @Column(nullable = false)
    private String mediaUrl; // Stores Firebase URL

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType; // PHOTO or VIDEO

}
