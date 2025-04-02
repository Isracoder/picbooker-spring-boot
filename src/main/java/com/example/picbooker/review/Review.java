package com.example.picbooker.review;

import java.time.LocalDateTime;

import com.example.picbooker.client.Client;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "client", "photographer" }))
@Builder
public class Review {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    @Column
    @Min(1)
    @Max(5)
    private Double rating;

    @Column
    private String comment;

    @Column
    private LocalDateTime leftAt;

}
