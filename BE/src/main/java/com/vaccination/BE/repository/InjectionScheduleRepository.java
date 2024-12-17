package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineInjectionSchedule;
import com.vaccination.BE.entity.VaccineVaccine;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InjectionScheduleRepository extends JpaRepository<VaccineInjectionSchedule, Long> {
    @Query("SELECT i FROM VaccineInjectionSchedule i WHERE STR(i.id) LIKE %:idPart%")
    Page<VaccineInjectionSchedule> findByIdContaining(@Param("idPart") String idPart, Pageable pageable);

    @Query("SELECT i FROM VaccineInjectionSchedule i WHERE STR(i.vaccine.vaccineName) LIKE %:namePart% and i.vaccine.status=:status")
    Page<VaccineInjectionSchedule> findByVaccineNameContaining(@Param("namePart")String vaccineName, Pageable pageable,@Param("status") boolean status);
    @Query("SELECT i.injectPerDay FROM VaccineInjectionSchedule i WHERE i.vaccine.id =:id")
    int getByVaccineId(@Param("id")long id);

    @Query("SELECT v FROM VaccineInjectionSchedule v WHERE v.vaccine.status = :status")
    Page<VaccineInjectionSchedule> findAllScheduleTrue(Pageable pageable, @Param("status") boolean status);

    @Query("SELECT v FROM VaccineInjectionSchedule v WHERE v.vaccine.status = :status and v.startDate<= current_date and v.endDate>= current_date")
    Page<VaccineInjectionSchedule> findAllScheduleTrueAndOpen(Pageable pageRequest, @Param("status") boolean status);

    @Query("SELECT i FROM VaccineInjectionSchedule i WHERE STR(i.vaccine.vaccineName) LIKE %:namePart% and i.vaccine.status=:status and i.startDate<= current_date and i.endDate>= current_date")
    Page<VaccineInjectionSchedule> findByVaccineNameContainingAndOpen(@Param("namePart")String nameOrId, Pageable pageRequest, @Param("status") boolean status);
}
