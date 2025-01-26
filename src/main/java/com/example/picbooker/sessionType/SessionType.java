package com.example.picbooker.sessionType;

import java.util.ArrayList;
import java.util.List;

import com.example.picbooker.photographer_sessionType.PhotographerSessionType;

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
public class SessionType {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Default
    @OneToMany(mappedBy = "sessionType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PhotographerSessionType> photographerSessionTypes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column
    private SessionTypeName type;

    @Column
    private String description;

}
