package com.example.picbooker.deposit;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    public void create() {
        // to do implement ;
    }

    public Optional<Deposit> findById(Long id) {
        return depositRepository.findById(id);
    }

    public Deposit findByIdThrow(Long id) {
        return depositRepository.findById(id).orElseThrow();
    }

    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

}
