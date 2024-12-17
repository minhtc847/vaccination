package com.vaccination.BE.repository;

import com.vaccination.BE.dto.response.registerInject_response.RegisterInjecctionResponse;
import com.vaccination.BE.entity.RegisterInject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegisterInjectRepository extends JpaRepository<RegisterInject,Long> {


    @Query("SELECT COUNT(r) FROM RegisterInject r WHERE r.dateInject = :dateInject")
    int countByDay(@Param("dateInject") LocalDate dateInject);

    @Query("SELECT r FROM RegisterInject r WHERE r.VaccineEmployee.id = :cusId AND r.dateInject = :date")
    Page<RegisterInject> findByCusIdAndDate(@Param("cusId") long cusId, @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT r FROM RegisterInject r WHERE r.VaccineEmployee.id = :cusId ")
    Page<RegisterInject> findByCusId(@Param("cusId") long cusId, Pageable pageable);
    @Query("SELECT r FROM RegisterInject r WHERE r.VaccineEmployee.id = :cusId AND r.vaccineInjectionSchedule.id = :scheduleId")
    RegisterInject findByCusIdAndScheduleId(@Param("cusId") Long customerId, @Param("scheduleId") Long scheduleId);

    @Query("SELECT r FROM RegisterInject r WHERE r.vaccineInjectionSchedule.id = :scheduleId AND r.dateInject = :now AND r.result= :result")
    List<RegisterInject> findByScheIdAndDate(@Param("scheduleId") long scheduleId, @Param("now") LocalDate now,@Param("result") boolean boo);
    @Query("SELECT r FROM RegisterInject r WHERE r.VaccineEmployee.username = :customerName AND r.vaccineInjectionSchedule.id = :scheduleId")
    Optional<RegisterInject> findByCusNameAndScheduleId(@Param("customerName")String customerName, @Param("scheduleId") Long scheduleId);

    @Query("SELECT r FROM RegisterInject r WHERE r.VaccineEmployee.username = :customerName AND r.vaccineInjectionSchedule.id = :scheduleId AND :startDate <= r.dateInject AND r.dateInject <= :endDate")
    Optional<RegisterInject> findUser(@Param("customerName") String username, @Param("scheduleId") Long injectionScheduleId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    @Query("SELECT COUNT(r) FROM RegisterInject r WHERE r.dateInject = :dateInject AND r.vaccineInjectionSchedule.vaccine.id =:vaccineId")
    int countByDay(@Param("dateInject") LocalDate dateInject,@Param("vaccineId") long vaccineId);

}
