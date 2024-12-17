package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineVaccineType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccineTypeRepository extends JpaRepository<VaccineVaccineType, Long> {
    @Query("SELECT t FROM VaccineVaccineType t WHERE STR(t.id) LIKE %:idPart%")
    Page<VaccineVaccineType> findByIdContaining(@Param("idPart") String idPart, Pageable pageable);

    @Query("SELECT t FROM VaccineVaccineType t WHERE STR(t.vaccineTypeName) LIKE %:namePart%")
    Page<VaccineVaccineType> findByVaccineTypeNameContaining(@Param("namePart")String vaccineTypeName, Pageable pageable);
    @Query("SELECT t FROM VaccineVaccineType t WHERE t.id = :id")
    VaccineVaccineType findByVaccineTypeId(@Param("id") long id);
    boolean existsByVaccineTypeName(String vaccineTypeName);

    boolean findByVaccineTypeName(String vaccineTypeName);

    @Query("SELECT v FROM VaccineVaccineType v WHERE v.status = true")
    List<VaccineVaccineType> findAllExceptInactive();
}
