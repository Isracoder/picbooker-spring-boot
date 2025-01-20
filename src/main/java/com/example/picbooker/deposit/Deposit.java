package com.example.picbooker.deposit;

import java.time.LocalDateTime;

import com.example.picbooker.session.Session;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Deposit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // session id
    @OneToOne(mappedBy = "deposit")
    private Session session;

    @Column
    private Double amount;

    @Column
    private String currency; // to do look into

    @Column
    private LocalDateTime paidAt;

    @Column
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

}
