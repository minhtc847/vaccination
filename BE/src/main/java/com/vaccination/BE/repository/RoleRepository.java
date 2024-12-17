package com.vaccination.BE.repository;

import com.vaccination.BE.entity.VaccineRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<VaccineRole,Long> {
    Optional<VaccineRole> findByName(String name);
}
