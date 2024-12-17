package com.vaccination.BE.dto.request.vaccine_type_request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTypeRequest {
    @NotBlank(message = "Description is required!")
    @Size(max=200 ,message = "Description must be less than 200 characters")
    String description;

    @NotBlank(message = "Vaccine Type Name is required!")
    @Size(max=50 ,message = "Vaccine Type Name must be less than 50 characters")
    String vaccineTypeName;

    String image;

    Boolean status;

    MultipartFile file;
}
