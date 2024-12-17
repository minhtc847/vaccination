package com.vaccination.BE.dto.request.employee_request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.time.Period;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeUpdateRequest {

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 100, message = "Address must be less than 100 characters")
    private String address;

    @NotNull(message = "Date of birth cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "Cannot input future date ")
    @EmployeeUpdateRequest.Age(min = 18, max = 65, message = "The age is not valid, please check date of birth!")
    private LocalDate dateOfBirth;


    @Email(message = "Wrong email!")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Employee name cannot be blank")
    private String employeeName;

    @NotBlank(message = "Gender cannot be blank")
    private String gender;

    //    @NotBlank(message = "Image cannot be blank")
    private String image;

//    @Size(min = 8, message = "Password must be at least 8 characters")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
//    @NotBlank(message = "Password cannot be blank")
//    private String password;

    @Pattern(regexp = "^0[0-9]{9,13}$", message = "Wrong phone number!")
    @NotBlank(message = "Phone cannot be blank")

    private String phone;

    @NotBlank(message = "Position cannot be blank")
    private String position;

//    @Size(min = 8, message = "Username must be at least 8 characters")
//    @NotBlank(message = "Username cannot be blank")
//    private String username;

    @NotBlank(message = "Working place cannot be blank")
    private String workingPlace;

    private MultipartFile file;
    private Long version;

    // Custom annotation for age validation
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = AgeValidator.class)
    @Target({ElementType.FIELD})
    public @interface Age {
        String message() default "Invalid age";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

        int min();

        int max();
    }

    public static class AgeValidator implements ConstraintValidator<Age, LocalDate> {
        private int minAge;
        private int maxAge;

        @Override
        public void initialize(Age age) {
            this.minAge = age.min();
            this.maxAge = age.max();
        }

        @Override
        public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
            if (dateOfBirth == null) {
                return false;
            }
            LocalDate today = LocalDate.now();
            int age = Period.between(dateOfBirth, today).getYears();
            return age >= minAge && age <= maxAge;
        }
    }
}
