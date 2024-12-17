package com.vaccination.BE.service.vaccineReport;

import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.dto.response.vaccine_report_response.SearchVaccineReportResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VaccineReportService {
    SearchVaccineReportResponse getListVaccineReport(int pageNo, int pageSize, String sortBy, String sortDir, String origin, String vaccineTypeName);
    Map<String, Integer> getVaccineGraphData(int year, String vaccineName);
    List<Integer> getAllYear();
}
