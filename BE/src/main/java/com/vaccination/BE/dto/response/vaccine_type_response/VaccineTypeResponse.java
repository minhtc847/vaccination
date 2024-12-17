package com.vaccination.BE.dto.response.vaccine_type_response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTypeResponse {
    long id;
    String code;
    String description;
    String vaccineTypeName;
    String image;
    boolean status;
}
