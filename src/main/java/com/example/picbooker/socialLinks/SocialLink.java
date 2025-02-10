package com.example.picbooker.socialLinks;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = { "photographer", "platform" })
})
public class SocialLink {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // photographer id
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @Column(nullable = false)
    @NotNull
    private String linkUrl;

    @Column(nullable = false)
    @NotNull
    private Platform platform;

}
