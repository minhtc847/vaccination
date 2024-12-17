package com.vaccination.BE.mapper;

import com.vaccination.BE.dto.request.employee_request.EmployeeRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeRequest;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineVaccineType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VaccineTypeMapper {
    VaccineVaccineType toVaccineType(VaccineTypeRequest request);

    void vaccineTypeToVaccineTypeResponse(@MappingTarget VaccineTypeResponse vaccineTypeResponse, VaccineVaccineType vaccineVaccineType);
    VaccineTypeResponse toVaccineResponse(VaccineVaccineType vaccineType);

}
