package com.vaccination.BE.controller;

import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.service.injectionReport.InjectionReportService;
import com.vaccination.BE.utils.AppConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/injectionreport")
public class InjectionReportController {
    InjectionReportService injectionReportService;

    public InjectionReportController(InjectionReportService injectionReportService) {
        this.injectionReportService = injectionReportService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchInjectionReportResponse getListInjectionReport(
            @RequestParam(name = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNo,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
            @RequestParam(name = "usage",required = false) String usage,
            @RequestParam(name = "dateFrom",required = false)LocalDate injectDateFrom,
            @RequestParam(name = "dateTo",required = false) LocalDate injectDateTo,
            @RequestParam(name = "vaccineType",required = false)String vaccineTypeName
    ) {

        return injectionReportService.getListInjectionReport(pageNo,pageSize,sortBy,sortDir,usage,injectDateFrom, injectDateTo, vaccineTypeName);
    }

    @GetMapping("graph/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public Map<String, Integer> graphVaccineReport(@PathVariable int year){
       return injectionReportService.getInjectionGraphData(year);
    }

    @GetMapping("getAllYear")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public List<Integer> getAllyear(){
        return injectionReportService.getAllYear();
    }
}
