package com.vaccination.BE.dto.request.customer_request;


import com.vaccination.BE.dto.request.employee_request.EmployeeUpdateRequest;
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
public class CustomerRequest {
    @NotNull(message = "FullName cannot be null")
    String employeeName;
    @NotNull(message = "Date of birth cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Cannot input future date ")
    @EmployeeUpdateRequest.Age(min = 18,max=100, message = "The age is not valid, please check date of birth!")
    LocalDate dateOfBirth;
    @NotBlank(message = "Gender cannot be blank")
    String gender;

    @NotBlank(message = "Identity Card cannot be blank")
    String identityCard;

    @NotBlank(message = "Address cannot be blank")
    private String Address;

    @Size(min = 8, message = "Username must be at least 8 characters")
    @NotBlank(message = "Username cannot be blank")
    String username;

    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    @NotBlank(message = "Password cannot be blank")
    String password;

    String passwordConfirm;

    @Email(message = "Wrong email!")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Pattern(regexp = "^0[0-9]{9,13}$", message = "Wrong phone number!")
    @NotBlank(message = "Phone cannot be blank")
    private String phone;
}
