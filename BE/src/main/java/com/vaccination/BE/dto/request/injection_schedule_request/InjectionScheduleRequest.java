package com.vaccination.BE.dto.request.injection_schedule_request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InjectionScheduleRequest {
    @NotNull(message = "Vaccine name is required!")
    private Long vaccineId;

    @Size(max=200 ,message = "Note must be less than 200 characters")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "From date is required!")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End date is required!")
    private LocalDate endDate;

    @NotNull(message = "Place is required!")
    @NotBlank(message = "Place is required!")
    @Size(max=100 ,message = "Street address must be less than 200 characters")
    private String place;

    @NotNull(message = "Injection Times is required!")
    @Min(value = 1, message = "Injection Times must be greater than or equal to 1.")
    @Max(value = 10000, message = "Injection Times must be less than 10000")
    private int injectionTimes;

    @NotNull(message = "Injection per day required!")
    @Min(value = 1, message = "Injection per day must be greater than or equal to 1.")
    @Max(value = 10000, message = "Injection per day must be less than 10000")
    private int injectPerDay;

}
