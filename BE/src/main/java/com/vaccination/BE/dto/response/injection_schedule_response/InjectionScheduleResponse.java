package com.vaccination.BE.dto.response.injection_schedule_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InjectionScheduleResponse {
    private long id;
    private long vaccineId;
    private String vaccineName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String place;
    private int injectPerDay;
    private int injectionTimes;
    private String description;
    private String status;
}
