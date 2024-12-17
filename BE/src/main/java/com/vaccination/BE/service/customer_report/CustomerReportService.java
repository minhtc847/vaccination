package com.vaccination.BE.service.customer_report;

import com.vaccination.BE.dto.response.customer_report_response.SearchCustomerReportResponse;

import java.time.LocalDate;

public interface CustomerReportService {
    SearchCustomerReportResponse getListCustomerReport(int pageNo, int pageSize, String sortBy, String sortDir, LocalDate dateFrom, LocalDate dateTo, String fullName, String address);
}
