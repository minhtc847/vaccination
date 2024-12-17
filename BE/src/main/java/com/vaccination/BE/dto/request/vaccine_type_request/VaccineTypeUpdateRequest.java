package com.vaccination.BE.dto.request.vaccine_type_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTypeUpdateRequest {
    //    String id;

    @NotBlank(message = "Please input description")
    @Size(max=199 ,message = "Description must be less than 200 characcter")
    String description;

    @NotBlank(message = "Please input name")
    @Size(max=49 ,message = "vaccine type name must be less than 50 characcter")
    String vaccineTypeName;

    String image;

    @NotNull(message = "Please input status")
    Boolean status;

    MultipartFile file;
}
