package com.vaccination.BE.dto.response.vaccine_injection_result_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {
    private List<CustomerCreateResponse> customer;
    private String vaccineName;
    private String vaccineType;
    private int injection;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate injectionDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate injectionNextDate;
    private String Schedule;
}
