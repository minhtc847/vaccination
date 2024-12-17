package com.vaccination.BE.mapper;
import com.vaccination.BE.dto.response.injection_report_response.InjectionReportResponse;
import com.vaccination.BE.entity.VaccineInjectionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface InjectionReportMapper {
    InjectionReportMapper INSTANCE = Mappers.getMapper(InjectionReportMapper.class);
    @Mapping(source = "vaccine.vaccineName", target = "vaccineName")
    @Mapping(source = "vaccine.usage", target = "usage")
    @Mapping(source = "customer.employeeName", target = "customerName")
    @Mapping(source = "injectionDate", target = "dateOfInjection")
    InjectionReportResponse toVaccineReportResponse(VaccineInjectionResult vaccineInjectionResult);
}
