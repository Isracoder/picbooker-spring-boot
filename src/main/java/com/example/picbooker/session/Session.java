package com.example.picbooker.session;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Currency;

import com.example.picbooker.client.Client;
import com.example.picbooker.deposit.Deposit;
import com.example.picbooker.photographer.Photographer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
    private Double totalPrice;

    @Column
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column
    private SessionStatus status;

    // relationships
    // deposit , client , sessiontype , photographerId

    @ManyToOne
    @JoinColumn(name = "client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "photographer", nullable = false)
    private Photographer photographer;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "deposit_id", referencedColumnName = "id")
    private Deposit deposit;

    // many to many with photographerAddOn
}
