package com.vaccination.BE.mapper;

import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultRequest;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.InjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.VaccineInjectionResultResponse;
import com.vaccination.BE.entity.VaccineInjectionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface VaccineInjectionResultMapper {
    @Mappings({
            @Mapping(target = "customerId", source = "customer.id"),
            @Mapping(target = "injectionDate", source = "injectionDate"),
            @Mapping(target = "result", ignore = true), // Handle as needed
            @Mapping(target = "vaccineId", source = "vaccine.id"),
            @Mapping(target = "scheduleId", source = "injectionSchedule.id"),
            @Mapping(target = "place",  ignore = true)
    })
    void vaccineInjectionResultToVaccineInjectionResultResponse(@MappingTarget VaccineInjectionResultResponse vaccineInjectionResultResponse, VaccineInjectionResult vaccineInjectionResult);
    @Mappings({
            @Mapping(target = "id",ignore = true),
            @Mapping(target = "customer", ignore = true), // Handle as needed
            @Mapping(target = "injectionDate", expression = "java(getCurrentDate())"),
            @Mapping(target = "vaccine", ignore = true), // Handle as needed
            @Mapping(target = "injectionSchedule", ignore = true) // Handle as needed
    })
    void updateVaccineInjectionResult(@MappingTarget VaccineInjectionResult vaccineInjectionResult, VaccineInjectionResultRequest request);

    @Mappings({
//            @Mapping(target = "id", ignore = true),
//            @Mapping(target = "customer", source = "customer"), // Assuming CustomerResponse mapper is defined
//            @Mapping(target = "injectionDate", source = "injectionDate"),
            @Mapping(target = "nextInjectionDate", ignore = true), // Handle as needed
            @Mapping(target = "injectionTime", ignore = true), // Handle as needed
//            @Mapping(target = "vaccine", source = "vaccine"), // Maps using VaccineMapper
//            @Mapping(target = "injectionSchedule", source = "injectionSchedule") // Maps using VaccineInjectionScheduleMapper
    })
    void vaccineInjectionResultToInjectionResultResponse(@MappingTarget InjectionResultResponse injectionResultResponse, VaccineInjectionResult vaccineInjectionResult);
    default LocalDate getCurrentDate() {
        return LocalDate.now();
    }

}
