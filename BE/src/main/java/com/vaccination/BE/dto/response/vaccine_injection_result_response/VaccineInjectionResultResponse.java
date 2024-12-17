package com.vaccination.BE.dto.response.vaccine_injection_result_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccineInjectionResultResponse {
//    private Long id;
    private Long customerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate injectionDate;
    private String result;
    private Long vaccineId;
    private Long scheduleId;
    private String place;
}
