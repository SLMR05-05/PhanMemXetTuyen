package com.xettuyen.backendapi.repository;

import com.xettuyen.backendapi.entity.NguyenVongXettuyen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NguyenVongRepository extends JpaRepository<NguyenVongXettuyen, Integer> {

    List<NguyenVongXettuyen> findByNnCccdOrderByNvTtAsc(String nnCccd);

    Optional<NguyenVongXettuyen> findByNnCccdAndNvTt(String nnCccd, Integer nvTt);

    boolean existsByNnCccdAndNvMaNganh(String nnCccd, String nvMaNganh);

    boolean existsByNnCccdAndNvTt(String nnCccd, Integer nvTt);
}