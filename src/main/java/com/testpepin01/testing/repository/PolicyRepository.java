package com.testpepin01.testing.repository;

import com.testpepin01.testing.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query("SELECT p FROM Policy p WHERE p.vehicleNumber = ?1")
    public Policy findByVehicleNumber(String vehicleNumber);

    @Modifying
    @Transactional
    @Query("DELETE FROM Policy p WHERE p.vehicleNumber = ?1")
    public void removePolicy(String vehicleNumber);
}