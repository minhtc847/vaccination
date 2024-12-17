package com.vaccination.BE.mapper;
import com.vaccination.BE.dto.request.employee_request.EmployeeRequest;
import com.vaccination.BE.dto.request.employee_request.EmployeeUpdateRequest;
import com.vaccination.BE.dto.response.customer_report_response.CustomerReportResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeUpdateResponse;
import com.vaccination.BE.dto.response.vaccine_report_response.VaccineReportResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineVaccine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "id", ignore = true) // Assuming ID is auto-generated and shouldn't be set
    @Mapping(target = "password", ignore = true) // Assuming password is not included in request
    @Mapping(target = "identityCard", ignore = true) // Assuming identityCard is not included in request
    @Mapping(target = "roles", ignore = true) // Assuming roles are managed separately
    @Mapping(target = "version", ignore = true) // Assuming version is managed separately
    @Mapping(target = "username", ignore = true) // Assuming username is handled separately
    VaccineEmployee toEmployee(EmployeeRequest request);
    @Mapping(target = "id", ignore = true) // Assuming employeeId in DTO maps to id in entity
    @Mapping(target = "password", ignore = true) // Assuming password is not updated
    @Mapping(target = "identityCard", ignore = true) // Assuming identityCard is not updated
    @Mapping(target = "roles", ignore = true) // Assuming roles are managed separately
    @Mapping(target = "version", source = "version") // Direct mapping for version
    @Mapping(target = "username", ignore = true) // Assuming username is handled separately
    void mmployeeUpdateRequestupdateEmployee(@MappingTarget VaccineEmployee VaccineEmployee , EmployeeUpdateRequest request);
    @Mapping(target = "employeeId", source = "id") // Map id to employeeId
    @Mapping(target = "version", source = "version") // Map version
    void employeeToEmployeeUpdateResponse (@MappingTarget EmployeeUpdateResponse employeeUpdateResponse, VaccineEmployee VaccineEmployee);
    @Mapping(target = "employeeId", source = "id") // Map id to employeeId
    @Mapping(target = "version", source = "version") // Map version
    void employeeToEmployeeResponse(@MappingTarget EmployeeResponse employeeResponse ,VaccineEmployee VaccineEmployee);

}
