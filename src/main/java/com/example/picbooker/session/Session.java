package com.example.picbooker.session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import com.example.picbooker.client.Client;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.photographer_additionalService.PhotographerAddOn;
import com.example.picbooker.photographer_sessionType.PhotographerSessionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
public class Session {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private LocalDate date;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;

    @Column
    private String location;

    @Column
    private String privateComment;

    @Column
    private Double totalPrice;

    @Column
    private Currency currency;

    @Column
    private String clientName;

    @Column
    private String clientEmail;

    @Enumerated(EnumType.STRING)
    @Column
    private SessionStatus status;

    // relationships
    // deposit , client , sessionType, Addon , photographerId

    @ManyToOne
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "sessionType", nullable = false)
    private PhotographerSessionType sessionType;

    @ManyToOne
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deposit_id", referencedColumnName = "id")
    private Deposit deposit;

    @Default
    @ManyToMany
    @JoinTable(name = "add_ons", joinColumns = @JoinColumn(name = "addOn_id"), inverseJoinColumns = @JoinColumn(name = "session_id"))
    private Set<PhotographerAddOn> sessionAddOns = new HashSet<>();

}
