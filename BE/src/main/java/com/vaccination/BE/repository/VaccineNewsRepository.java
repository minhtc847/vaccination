package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineNew;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccineNewsRepository extends JpaRepository<VaccineNew, Long> {
    @Query("select v from VaccineNew v where v.title like %:namePart%")
    Page<VaccineNew> findByVaccineNameContaining(@Param("namePart")String name, Pageable pageable);

    @Query("SELECT v FROM VaccineNew v")
    List<VaccineNew> findAllById();
}
