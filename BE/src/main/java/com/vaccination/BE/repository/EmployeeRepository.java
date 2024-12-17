package com.vaccination.BE.repository;

import com.vaccination.BE.dto.response.customer_report_response.CustomerReportResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<VaccineEmployee, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<VaccineEmployee> findByUsername(String s);

    @Query("SELECT e FROM VaccineEmployee e JOIN e.roles r WHERE r.name = :role")
    Page<VaccineEmployee> findByRoles(@Param("role") String role, Pageable pageable);

    @Query("SELECT e FROM VaccineEmployee e JOIN e.roles r WHERE STR(e.id) LIKE %:idPart% and  r.name = :role")
    Page<VaccineEmployee> findByIdContaining(@Param("idPart") String idPart, @Param("role") String role, Pageable pageable);

    @Query("SELECT e FROM VaccineEmployee e JOIN e.roles r WHERE STR(e.employeeName) LIKE %:namePart% and  r.name = :role")
    Page<VaccineEmployee> findByEmployeeNameContaining(@Param("namePart") String employeeName, @Param("role") String role, Pageable pageable);

    Optional<VaccineEmployee> findById(Long id);

    Optional<VaccineEmployee> findByEmail(String email);

    Optional<VaccineEmployee> findByPhone(String phone);

    boolean existsByIdentityCard(String indentityCard);

    @Query("SELECT e FROM VaccineEmployee e JOIN e.roles r WHERE r.name LIKE %:role%")
    List<VaccineEmployee> findByRoleId(@Param("role") String role);

    @Query("SELECT e FROM VaccineEmployee e WHERE e.id = :id AND e.position LIKE %:position%")
    Optional<VaccineEmployee> findByIdAndPosition(@Param("id") Long id, @Param("position") String position);

    @Query("SELECT e FROM VaccineEmployee e WHERE e.username = :customerName AND e.position LIKE %:position%")
    Optional<VaccineEmployee> findByUsernameAndPosition(@Param("customerName") String customerName, @Param("position") String customer);

    //Filter
    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND :dateTo " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndDateTo(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND current_date " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFrom(@Param("dateFrom") LocalDate dateFrom, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN (SELECT MIN(v2.dateOfBirth) FROM VaccineEmployee v2) AND :dateTo " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateTo(@Param("dateTo") LocalDate dateTo, Pageable pageRequest);


    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "JOIN VaccineInjectionResult vir ON ve.id = vir.customer.id " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND :dateTo AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndDateToAndAddress(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND current_date AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndAddress(@Param("dateFrom") LocalDate dateFrom, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN (SELECT MIN(v2.dateOfBirth) FROM VaccineEmployee v2) AND :dateTo AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateToAndAddress(@Param("dateTo") LocalDate dateTo, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findAllCustomerAndAdress(@Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND :dateTo AND ve.employeeName LIKE %:fullName% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndDateToAndFullName(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("fullName") String fullName, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND current_date AND ve.employeeName LIKE %:fullName% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndFullName(@Param("dateFrom") LocalDate dateFrom, @Param("fullName") String fullName, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN (SELECT MIN(v2.dateOfBirth) FROM VaccineEmployee v2) AND :dateTo AND ve.employeeName LIKE %:fullName% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateToAndFullName(@Param("dateTo") LocalDate dateTo, @Param("fullName") String fullName, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.employeeName LIKE %:fullName% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findAllCustomerAndFullName(@Param("fullName") String fullName, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND :dateTo AND ve.employeeName LIKE %:fullName% AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndDateToAndFullNameAndAddress(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("fullName") String fullName, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN :dateFrom AND current_date AND ve.employeeName LIKE %:fullName% AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateFromAndFullNameAndAddress(@Param("dateFrom") LocalDate dateFrom, @Param("fullName") String fullName, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.dateOfBirth BETWEEN (SELECT MIN(v2.dateOfBirth) FROM VaccineEmployee v2) AND :dateTo AND ve.employeeName LIKE %:fullName% AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findByDateToAndFullNameAndAddress(@Param("dateTo") LocalDate dateTo, @Param("fullName") String fullName, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles er " +
            "WHERE er.id = 3 AND ve.employeeName LIKE %:fullName% AND ve.address LIKE %:address% " +
            "GROUP BY ve.id, ve.employeeName, ve.dateOfBirth, ve.address, ve.identityCard")
    Page<VaccineEmployee> findAllCustomerAndFullNameAndAddress(@Param("fullName") String fullName, @Param("address") String address, Pageable pageRequest);

    @Query("SELECT ve " +
            "FROM VaccineEmployee ve " +
            "JOIN ve.roles r " +
            "WHERE r.id = :roleId")
    Page<VaccineEmployee> findAllCustomerWithRoleCustomer(@Param("roleId") int roleId, Pageable pageRequest);

}
