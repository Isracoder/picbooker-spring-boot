package com.example.picbooker.notification;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.picbooker.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User recipient;
    // to think of changing to photographer/client to ensure that part only receives
    // it

    @Column
    private String message;

    @Column
    @Default
    private boolean read = false; // Track if the notification has been seen

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;
}
