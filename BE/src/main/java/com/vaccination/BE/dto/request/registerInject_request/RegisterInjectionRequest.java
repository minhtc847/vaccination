package com.vaccination.BE.dto.request.registerInject_request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterInjectionRequest {
    @NotNull(message = "Please input username")
    @NotEmpty(message = "Please input username")
    String username;

    @NotNull(message = "Please input registerInjectId")
    Long injectionScheduleId;
//    @NotNull(message = "Please input place")
//    @NotEmpty(message = "Please input place")
//    String place;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    String[] contrain;
}
