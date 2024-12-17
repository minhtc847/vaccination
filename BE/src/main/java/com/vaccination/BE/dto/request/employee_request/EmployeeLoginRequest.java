package com.vaccination.BE.dto.request.employee_request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeLoginRequest {
    @NotBlank(message = "Username must be not empty!")
    String username;
    @NotBlank(message = "Password must be not empty!")
    String password;

}
