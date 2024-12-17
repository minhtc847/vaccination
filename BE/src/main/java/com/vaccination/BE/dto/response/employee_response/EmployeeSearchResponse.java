package com.vaccination.BE.dto.response.employee_response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeSearchResponse {
    List<EmployeeResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
