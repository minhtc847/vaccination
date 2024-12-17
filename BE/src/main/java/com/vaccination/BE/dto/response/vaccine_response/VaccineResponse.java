package com.vaccination.BE.dto.response.vaccine_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.entity.VaccineVaccineType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineResponse {
    long id;
    long vaccineTypeId;
    String vaccineName;
    String usage;
    String indication;
    Integer numberOfInjection;
    Integer timeBeginNextInjection;
    String origin;
    boolean status;
    Integer totalInject;
    String vaccineTypeName;
    String[] contraindication;
}
