package com.vaccination.BE.mapper;

import ch.qos.logback.core.model.ComponentModel;
import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.entity.VaccineRule;
import com.vaccination.BE.entity.VaccineVaccine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VaccineMapper {
    VaccineVaccine toVaccine(VaccineRequest request);

    void vaccineToVaccineResponse(@MappingTarget VaccineResponse vaccineResponse, VaccineVaccine vaccineVaccine);

    void updateVaccine(@MappingTarget VaccineVaccine vaccine, VaccineRequest request);

}
