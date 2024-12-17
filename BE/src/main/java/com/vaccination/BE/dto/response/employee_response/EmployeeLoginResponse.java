package com.vaccination.BE.dto.response.employee_response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeLoginResponse {
    String token;
    boolean authenticated;
}
