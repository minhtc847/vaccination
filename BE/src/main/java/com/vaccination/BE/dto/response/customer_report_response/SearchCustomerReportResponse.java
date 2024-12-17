package com.vaccination.BE.dto.response.customer_report_response;

import com.vaccination.BE.entity.VaccineEmployee;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchCustomerReportResponse {
    List<CustomerReportResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
