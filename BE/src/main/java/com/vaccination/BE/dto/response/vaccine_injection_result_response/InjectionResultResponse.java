package com.vaccination.BE.dto.response.vaccine_injection_result_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineInjectionSchedule;
import com.vaccination.BE.entity.VaccineVaccine;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InjectionResultResponse {
    private long id;
    private CustomerResponse customer;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate injectionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate nextInjectionDate;
    private int injectionTime;
    private VaccineResponse vaccine;
    private InjectionScheduleResponse injectionSchedule;
}
