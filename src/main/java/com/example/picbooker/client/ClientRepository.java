package com.example.picbooker.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.picbooker.user.User;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByUser(User user);
}
