package com.vaccination.BE.dto.response.vaccine_report_response;

import com.vaccination.BE.dto.response.injection_report_response.InjectionReportResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchVaccineReportResponse {
    List<VaccineReportResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
