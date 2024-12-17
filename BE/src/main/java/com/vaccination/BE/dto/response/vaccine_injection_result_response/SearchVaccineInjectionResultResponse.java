package com.vaccination.BE.dto.response.vaccine_injection_result_response;

import com.vaccination.BE.dto.response.news_response.NewsResponse;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchVaccineInjectionResultResponse {
    List<InjectionResultResponse> content;
    int pageNo;
    int pageSize;
    long totalElements;
    int totalPaged;
    boolean last;
}
