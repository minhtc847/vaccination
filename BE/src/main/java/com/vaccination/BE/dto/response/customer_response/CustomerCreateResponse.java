package com.vaccination.BE.dto.response.customer_response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.dto.request.employee_request.EmployeeUpdateRequest;
import jakarta.validation.constraints.*;
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
public class CustomerCreateResponse {
    String employeeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
    String gender;
    String identityCard;
    private String Address;
    String username;
    private String email;
    private String phone;
    private Integer version;
}
