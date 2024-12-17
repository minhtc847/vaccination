package com.vaccination.BE.dto.request.vaccine_injection_result_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VaccineInjectionResultForCreateRequest {
    @NotBlank(message = "Customer is required!")
    private List<String> customerName;

    @NotNull(message = "Schedule is required!")
    private Long scheduleId;

    @Size(max=100 ,message = "place must be less than 100 characters")
    private String place;
}
