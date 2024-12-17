package com.vaccination.BE.mapper;

import com.vaccination.BE.dto.request.injection_schedule_request.InjectionScheduleRequest;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.entity.VaccineInjectionSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InjectionScheduleMapper {
    @Mapping(target = "vaccineId", source = "vaccine.id") // Map vaccine ID from VaccineVaccine entity
    @Mapping(target = "status", ignore = true) // Ignore status if not available in entity
    void injectionScheduleToInjectionScheduleResponse(@MappingTarget InjectionScheduleResponse injectionScheduleResponse, VaccineInjectionSchedule vaccineInjectionSchedule);

//    @Mapping(target = "vaccine", source = "vaccineId") // Map vaccine ID from request to VaccineVaccine entity
    VaccineInjectionSchedule toInjectionSchedule(InjectionScheduleRequest request);

//    @Mapping(target = "vaccine", source = "vaccineId") // Map vaccine ID from request to VaccineVaccine entity
    void updateInjectionSchedule(@MappingTarget VaccineInjectionSchedule injectionSchedule,
                                 InjectionScheduleRequest request);
}
