package com.testpepin01.testing.repository;


import com.testpepin01.testing.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.username = ?1")
    public List<Vehicle> findByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE Vehicle v SET v.insuranceStatus = ?2 WHERE v.vehicleNumber = ?1")
    public void updateInsuranceStatus(String vehicleNumber, String insuranceStatus);

    @Modifying
    @Transactional
    @Query("DELETE FROM Vehicle v WHERE v.vehicleNumber = ?1")
    public void removeVehicle(String vehiclenumber);
}