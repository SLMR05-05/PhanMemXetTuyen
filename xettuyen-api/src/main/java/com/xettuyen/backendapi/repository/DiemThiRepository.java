package com.xettuyen.backendapi.repository;

import com.xettuyen.entity.DiemThiXettuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiemThiRepository extends JpaRepository<DiemThiXettuyen, Integer> {
    Optional<DiemThiXettuyen> findByCccd(String cccd);
}