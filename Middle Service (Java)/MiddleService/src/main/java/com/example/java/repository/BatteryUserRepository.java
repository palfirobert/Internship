package com.example.java.repository;

import com.example.java.domain.BatteryUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BatteryUserRepository extends JpaRepository<BatteryUser, Integer> {
    /**
     *
     * @param email The email used to find the user
     * @return The user
     */
    Optional<BatteryUser> findByEmail(String email);
}
