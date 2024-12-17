package com.vaccination.BE.controller;

import com.vaccination.BE.dto.response.customer_report_response.SearchCustomerReportResponse;
import com.vaccination.BE.service.customer_report.CustomerReportServiceImpl;
import com.vaccination.BE.utils.AppConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/customerreport")
public class CustomerReportController {

    CustomerReportServiceImpl customerReportService;

    public CustomerReportController(CustomerReportServiceImpl customerReportService) {
        this.customerReportService = customerReportService;
    }

    @GetMapping()
    //@PreAuthorize("hasRole('ADMIN')")
    public SearchCustomerReportResponse getListCustomerReport(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "dateFrom", required = false) LocalDate dateFrom,
            @RequestParam(name = "dateTo", required = false) LocalDate dateTo,
            @RequestParam(name = "fullName", required = false) String fullName,
            @RequestParam(name = "address", required = false) String address
    ) {
        return customerReportService.getListCustomerReport(pageNo, pageSize, sortBy, sortDir, dateFrom, dateTo, fullName, address);
    }

}
