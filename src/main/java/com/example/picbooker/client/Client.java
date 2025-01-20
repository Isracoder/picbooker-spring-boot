package com.example.picbooker.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.review.Review;
import com.example.picbooker.session.Session;
import com.example.picbooker.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table
public class Client extends User {

    // id inherited

    private int pointsBalance;

    // favorite photographers

    @Default
    @ManyToMany
    @JoinTable(name = "favorite_photographers", joinColumns = @JoinColumn(name = "photographer_id"), inverseJoinColumns = @JoinColumn(name = "client_id"))
    private Set<Photographer> favoritePhotographers = new HashSet<>();

    // booked sessions

    @Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Session> bookedSessions = new ArrayList<>();

    // reviews

    @Default
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Review> reviews = new ArrayList<>();

}

// Table: users (Base table)
// id, email, password, name, role, created_at, updated_at
// Table: clients
// id, user_id, preferences, loyalty_points
// Table: photographers
// id, user_id, portfolio_url, availability
