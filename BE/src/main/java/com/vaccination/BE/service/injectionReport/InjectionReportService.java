package com.vaccination.BE.service.injectionReport;

import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InjectionReportService {
    SearchInjectionReportResponse getListInjectionReport(int pageNo, int pageSize, String sortBy, String sortDir, String usage, LocalDate injectDateFrom, LocalDate injectDateTo, String vaccineTypeName);
    Map<String, Integer> getInjectionGraphData(int year);
    List<Integer> getAllYear();
}
