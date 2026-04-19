package com.xettuyen.backendapi.repository;

import com.xettuyen.entity.ThiSinhXettuyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThiSinhRepository extends JpaRepository<ThiSinhXettuyen, Integer> {

    Optional<ThiSinhXettuyen> findByCccd(String cccd);

    Optional<ThiSinhXettuyen> findByCccdAndPassword(String cccd, String password);
}