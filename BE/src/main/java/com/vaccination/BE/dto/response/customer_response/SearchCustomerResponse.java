package com.vaccination.BE.dto.response.customer_response;

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
public class SearchCustomerResponse {
    List<CustomerResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
