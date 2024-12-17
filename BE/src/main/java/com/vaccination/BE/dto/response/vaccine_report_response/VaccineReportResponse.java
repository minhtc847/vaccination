package com.vaccination.BE.dto.response.vaccine_report_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.entity.VaccineVaccineType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineReportResponse {
    String vaccineName;
    String vaccineType;
    int numberOfInjection;
    int totalInjection;
    int timeBeginNextInjection;
    String origin;
}
