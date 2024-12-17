package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineInjectionResult;
import com.vaccination.BE.entity.VaccineVaccine;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccineInjectionResultRepository extends JpaRepository<VaccineInjectionResult, Long> {


    @Query("SELECT COUNT(v) FROM VaccineInjectionResult v WHERE MONTH(v.injectionDate) = :month AND YEAR(v.injectionDate) = :year")
    Integer totalInjectionByMonthAndYear(@Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.customer.id = :customerId")
    List<VaccineInjectionResult> findByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VaccineInjectionResult v WHERE v.customer.id = :customerId")
    boolean existsByCustomerId(@Param("customerId") long customerId);

    @Query("SELECT MAX(v.injectionDate) FROM VaccineInjectionResult v WHERE v.customer.id = :id AND v.vaccine.id = :vac_id")
    LocalDate getLatestResults(@Param("id") long id, @Param("vac_id") long vac_id);

    @Query("SELECT MAX(v.injectionDate) FROM VaccineInjectionResult v WHERE v.customer.id = :id")
    LocalDate getLatestResults1(@Param("id") long id);

    @Query("SELECT COUNT(v) FROM VaccineInjectionResult v WHERE v.vaccine.id = :id")
    int countByVaccineIdCustom(@Param("id") long id);

    @Query("SELECT v.id FROM VaccineInjectionResult v WHERE STR(v.customer.employeeName) LIKE %:nameOrId%")
    long[] getAllId(@Param("nameOrId") String nameOrId);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE STR(v.customer.employeeName) LIKE %:nameOrId% AND v.vaccine.vaccineType.status = true")
    Page<VaccineInjectionResult> findByCustomerNameContaining(Pageable pageable,String nameOrId);

    @Query("SELECT v.id FROM VaccineInjectionResult v")
    long[] getAllId();

    // Filter injection report
    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status=true")
    Page<VaccineInjectionResult> findAllWithVaccineTypeTrue(Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status=true AND v.vaccine.vaccineType.vaccineTypeName LIKE %:name%")
    Page<VaccineInjectionResult> findByVaccineTypeName(@Param("name") String vaccineTypeName, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND STR(v.vaccine.usage) LIKE %:usage%")
    Page<VaccineInjectionResult> findAllWithVaccineUsage(@Param("usage") String usage, Pageable pageable);


    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.injectionDate BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineInjectionResult> findByInjectDateBetween(@Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.injectionDate BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineInjectionResult> findByUsageAndInjectDateBetween(@Param("name") String usage, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.injectionDate BETWEEN :injectDateFrom AND current_date")
    Page<VaccineInjectionResult> findByUsageAndInjectDateFrom(@Param("name") String name, @Param("injectDateFrom") LocalDate injectDateFrom, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.injectionDate BETWEEN (SELECT MIN(v2.injectionDate) FROM VaccineInjectionResult v2) AND :injectDateTo")
    Page<VaccineInjectionResult> findByUsageAndInjectDateTo(@Param("name") String name, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.vaccineType.vaccineTypeName LIKE %:vaccineTypeName% AND v.injectionDate BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineInjectionResult> findByVaccineTypeNameAndInjectDateBetween(@Param("vaccineTypeName") String vaccineTypeName, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.injectionDate BETWEEN :injectDateFrom AND current_date")
    Page<VaccineInjectionResult> findByInjectDateFrom(@Param("injectDateFrom") LocalDate injectDateFrom, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.injectionDate BETWEEN (SELECT MIN(v2.injectionDate) FROM VaccineInjectionResult v2) AND :injectDateTo")
    Page<VaccineInjectionResult> findByInjectDateTo(@Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.vaccineType.vaccineTypeName LIKE %:name% AND v.injectionDate BETWEEN :injectDateFrom AND current_date")
    Page<VaccineInjectionResult> findByVaccineTypeNameAndInjectDateFrom(@Param("name") String vaccineTypeName, @Param("injectDateFrom") LocalDate injectDateFrom, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.vaccineType.vaccineTypeName LIKE %:name% AND v.injectionDate BETWEEN (SELECT MIN(v2.injectionDate) FROM VaccineInjectionResult v2) AND :injectDateTo")
    Page<VaccineInjectionResult> findByVaccineTypeNameAndInjectDateTo(@Param("name") String vaccineTypeName, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.vaccine.vaccineType.vaccineTypeName LIKE %:type% AND v.injectionDate BETWEEN :injectDateFrom AND :injectDateTo")
    Page<VaccineInjectionResult> findByUsageAndVaccineTypeNameAndInjectDateBetween(@Param("name") String usage, @Param("type") String vaccineTypeName, @Param("injectDateFrom") LocalDate injectDateFrom, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.vaccine.vaccineType.vaccineTypeName LIKE %:type% AND v.injectionDate BETWEEN :injectDateFrom AND current_date")
    Page<VaccineInjectionResult> findByUsageAndVaccineTypeNameAndInjectDateFrom(@Param("name") String name, @Param("type") String vaccineTypeName, @Param("injectDateFrom") LocalDate injectDateFrom, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND v.vaccine.usage LIKE %:name% AND v.vaccine.vaccineType.vaccineTypeName LIKE %:type% AND v.injectionDate BETWEEN (SELECT MIN(v2.injectionDate) FROM VaccineInjectionResult v2) AND :injectDateTo")
    Page<VaccineInjectionResult> findByUsageAndVaccineTypeNameAndInjectDateTo(@Param("name") String name, @Param("type") String vaccineTypeName, @Param("injectDateTo") LocalDate injectDateTo, Pageable pageable);

    @Query("SELECT v FROM VaccineInjectionResult v WHERE v.vaccine.vaccineType.status = true AND STR(v.vaccine.usage) LIKE %:usage% AND STR(v.vaccine.vaccineType.vaccineTypeName) LIKE %:type%")
    Page<VaccineInjectionResult> findAllWithVaccineUsageAndVaccineTypeName(@Param("usage") String usage, @Param("type") String vaccineTypeName, Pageable pageable);

    @Query("SELECT DISTINCT YEAR(v.injectionDate) FROM VaccineInjectionResult v ORDER BY YEAR(v.injectionDate) ASC")
    List<Integer> findAllYears();
    @Query("SELECT COUNT(v) FROM VaccineInjectionResult v WHERE v.injectionDate = :date AND v.injectionSchedule.vaccine.id = :vaccineId")
    int countByDay(@Param("date") LocalDate now,@Param("vaccineId")long vaccineId );

    @Query("SELECT COUNT(vir.id) FROM VaccineInjectionResult vir WHERE vir.customer.id = :customerId")
    int countByCustomerId(@Param("customerId") Long customerId);
}
