package com.vaccination.BE.mapper;

import com.vaccination.BE.dto.response.vaccine_report_response.VaccineReportResponse;
import com.vaccination.BE.entity.VaccineVaccine;
import com.vaccination.BE.entity.VaccineVaccineType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface VaccineReportMapper {
    VaccineReportMapper INSTANCE = Mappers.getMapper(VaccineReportMapper.class);


    @Mapping(source = "vaccine.vaccineName", target = "vaccineName")
    @Mapping(source = "vaccine.vaccineType", target = "vaccineType")
    @Mapping(source = "vaccine.numberOfInjection", target = "numberOfInjection")
    @Mapping(source = "vaccine.totalInject", target = "totalInjection")
    @Mapping(source = "vaccine.timeBeginNextInjection", target = "timeBeginNextInjection")
    @Mapping(source = "vaccine.origin", target = "origin")
    VaccineReportResponse toVaccineReportResponse(VaccineVaccine vaccine);

    // Custom mapping method
    default String map(VaccineVaccineType value) {
        if (value == null) {
            return null;
        }
        return value.getVaccineTypeName();
    }
}
