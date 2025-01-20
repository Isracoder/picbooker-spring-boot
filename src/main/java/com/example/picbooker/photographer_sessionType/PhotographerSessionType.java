package com.example.picbooker.photographer_sessionType;

import java.io.Serializable;

import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.sessionType.SessionType;

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
public class PhotographerSessionType implements Serializable {

    @EmbeddedId
    private PhotographerSessionTypeId id;

    // photographer id , session type id
    // to do think do I need these relationships since I have an embedded id ?
    @ManyToOne
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @ManyToOne
    @JoinColumn(name = "session_type", nullable = false)
    private SessionType sessionType;

    @Column
    private Double perHour;

    @Column
    private String currency;

}
