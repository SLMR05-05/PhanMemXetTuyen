package com.xettuyen.backendapi.repository;

import com.xettuyen.backendapi.entity.Nganh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NganhRepository extends JpaRepository<Nganh, Integer> {

    List<Nganh> findAllByOrderByMaNganhAsc();

    Optional<Nganh> findByMaNganh(String maNganh);
}