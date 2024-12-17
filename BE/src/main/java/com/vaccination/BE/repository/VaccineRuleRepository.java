package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineRule;
import com.vaccination.BE.entity.VaccineVaccine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccineRuleRepository extends JpaRepository<VaccineRule, Long> {
    boolean existsByContraindication(String contraindication);

    @Query("SELECT v FROM VaccineRule v")
    List<VaccineRule> findAllById();


    @Query("SELECT v FROM VaccineRule v WHERE v.vaccine.id = :id")
    List<VaccineRule> findByVaccineId(@Param("id") long id);
    @Query("SELECT v.contraindication FROM VaccineRule v WHERE v.vaccine.id = :id")
    List<String> getRuleById(long id);
}
