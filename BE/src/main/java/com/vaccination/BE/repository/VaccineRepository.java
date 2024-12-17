package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineVaccine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineRepository extends JpaRepository<VaccineVaccine, Long> {

    boolean existsByVaccineName(String vaccineName);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineType.status=true")
    Page<VaccineVaccine> findAllWithVaccineTypeTrue(Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE STR(v.id) LIKE %:idPart% and v.vaccineType.status=true")
    Page<VaccineVaccine> findByIdContaining(@Param("idPart") String idPart, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE STR(v.vaccineName) LIKE %:namePart% and v.vaccineType.status=true")
    Page<VaccineVaccine> findByVaccineNameContaining(@Param("namePart") String vaccineName, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.status=true and v.vaccineType.status=true")
    List<VaccineVaccine> findAllActiveById();

    @Query("SELECT v FROM VaccineVaccine v WHERE v.timeBeginNextInjection BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineVaccine> findByInjectDateBetween(@Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineName LIKE %:name% AND v.timeBeginNextInjection BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineVaccine> findByVaccineNameContainingAndInjectDateBetween(@Param("name") String name, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineType.vaccineTypeName LIKE %:vaccineTypeName% AND v.timeBeginNextInjection BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineVaccine> findByVaccineTypeNameAndInjectDateBetween(@Param("vaccineTypeName") String vaccineTypeName, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    Optional<VaccineVaccine> findById(Long id);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineType.status=true AND v.origin LIKE %:namePart%")
    Page<VaccineVaccine> findByOriginContaining(@Param("namePart") String origin, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.origin LIKE %:name% AND v.timeBeginNextInjection BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineVaccine> findByOriginContainingAndInjectDateBetween(@Param("name") String origin, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineType.vaccineTypeName LIKE %:vaccineTypeName%")
    Page<VaccineVaccine> findByVaccineTypeName(@Param("vaccineTypeName") String vaccineTypeName, Pageable pageable);

    @Query("SELECT v FROM VaccineVaccine v WHERE v.vaccineType.status = true AND v.vaccineType.vaccineTypeName LIKE %:vaccineTypeName% AND v.origin LIKE %:origin%")
    Page<VaccineVaccine> findByVaccineTypeNameAndOrigin(@Param("vaccineTypeName") String vaccineTypeName, @Param("origin") String origin, Pageable pageable);

    @Query("SELECT COALESCE(SUM(v.totalInject), 0) " +
            "FROM VaccineVaccine v " +
            "JOIN v.vaccineInjectionResults vr " +
            "WHERE v.vaccineName LIKE %:namePart% " +
            "AND MONTH(vr.injectionDate) = :month " +
            "AND YEAR(vr.injectionDate) = :year")
    Integer totalInjectionByDateAndVaccineName(@Param("month") Integer month,
                                               @Param("year") Integer year,
                                               @Param("namePart") String namePart);

    @Query("SELECT DISTINCT YEAR(vr.injectionDate) FROM VaccineVaccine v JOIN v.vaccineInjectionResults vr ORDER BY YEAR(vr.injectionDate) ASC")
    List<Integer> findAllYears();

    @Query("SELECT COUNT(v) > 0 FROM VaccineVaccine v WHERE v.vaccineType.id = :id AND v.status = :isActive")
    boolean existsByVaccineTypeId(@Param("id") long id, @Param("isActive") boolean isActive);


}
