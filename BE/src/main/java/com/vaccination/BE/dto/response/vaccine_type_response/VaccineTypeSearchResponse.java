package com.vaccination.BE.dto.response.vaccine_type_response;

import com.vaccination.BE.dto.response.employee_response.EmployeeResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineTypeSearchResponse {
    List<VaccineTypeResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
