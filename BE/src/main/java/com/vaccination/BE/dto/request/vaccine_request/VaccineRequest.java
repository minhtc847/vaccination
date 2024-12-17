package com.vaccination.BE.dto.request.vaccine_request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineRequest {
    @NotNull(message = "Vaccine name is required!")
    @NotBlank(message = "Vaccine name is required!")
    @Size(max = 50, message = "vaccine name must be less than 50 characters")
    String vaccineName;

    @NotNull(message = "Vaccine type is required!")
    Long vaccineTypeId;

    //  @NotBlank(message = "Please ender usage")
    @NotNull(message = "Usage is required!")
    @NotBlank(message = "Usage is required!")
    @Size(max = 200, message = "usage must be less than 200 characters")
    String usage;

    // @NotBlank(message = "Please enter indication")
    @NotNull(message = "Indication is required!")
    @NotBlank(message = "Indication is required!")
    @Size(max = 200, message = "indication must be less than 200 characters")
    String indication;

    @Min(value = 0, message = "Number of injection must be greater than or equal to 0.")
    @NotNull(message = "Number of injection is required!")
    @Max(value = 10000, message = "Number of injection must be less than 10000")
    Integer numberOfInjection;

    @Min(value = 0, message = "Time begin next injection must be greater than or equal to 0.")
    @NotNull(message = "Time to begin next injection is required!")
    @Max(value = 10000, message = "Time begin next injection must be less than 10000")
    Integer timeBeginNextInjection;

    @Min(value = 0, message = "Total injection must be greater than or equal to 0.")
    @NotNull(message = "Total injection is required!")
    @Max(value = 10000, message = "Total injection must be less than 10000")
    Integer totalInject;

    //@NotBlank(message = "Please enter origin")
    @Size(max = 50, message = "origin must be less than 50 characters")
    @NotNull(message = "Origin is required!")
    @NotBlank(message = "Origin is required!")
    String origin;

    String[] contraindication;

    boolean status;
}
