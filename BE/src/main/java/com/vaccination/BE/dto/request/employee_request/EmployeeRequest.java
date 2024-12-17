package com.vaccination.BE.dto.request.employee_request;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeRequest {

    @NotBlank(message = "Street address is required!")
    @Size(max = 100, message = "Street address must be less than 100 characters")
    private String address;

    @NotNull(message = "Date of birth is required!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Cannot input future date ")
    @EmployeeUpdateRequest.Age(min = 18, max = 65, message = "The age is not valid, please check date of birth!")
    private LocalDate dateOfBirth;

    @Email(message = "Wrong email!")
    @NotBlank(message = "Email is required!")
    @Size(max = 100, message = "email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Employee name is required!")
    @Size(max = 100, message = "employee name must be less than 100 characters")
    private String employeeName;

    @NotBlank(message = "Gender is required!")
    private String gender;

    private String image;

    @Pattern(regexp = "^0[0-9]{9,13}$", message = "Wrong phone number!")
    @NotBlank(message = "Phone number is required!")
    private String phone;

    @NotBlank(message = "Position is required!")
    private String position;

    @Size(min = 8, message = "Username must be at least 8 characters")
    @Size(max = 255, message = "Username must be less than 255 characters")
    @NotBlank(message = "Username is required!")
    private String username;

    @Size(max = 255, message = "Working place must be less than 255 characters")
    @NotBlank(message = "Working place is required!")
    private String workingPlace;

    private MultipartFile file;
}
