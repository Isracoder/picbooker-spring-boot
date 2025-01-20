package com.example.picbooker.review;

import java.time.LocalDateTime;

import com.example.picbooker.client.Client;
import com.example.picbooker.photographer.Photographer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class Review {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // photographer id , client id

    // @EmbeddedId
    // private PlayerRankKey id;

    @ManyToOne
    @MapsId("playerId")
    @JoinColumn(name = "player_id", insertable = false, updatable = false)
    private Photographer photographer;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", insertable = false, updatable = false)
    private Client client;

    @Column
    @Min(1)
    @Max(5)
    private Integer rating;

    @Column
    private String comment;

    @Column
    private LocalDateTime leftAt;

}
