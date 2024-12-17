package com.vaccination.BE.controller;

import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.dto.response.vaccine_report_response.SearchVaccineReportResponse;
import com.vaccination.BE.service.vaccineReport.VaccineReportService;
import com.vaccination.BE.utils.AppConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vaccinereport")
public class VaccineReportController {
    VaccineReportService vaccineReportService;

    public VaccineReportController(VaccineReportService vaccineReportService) {
        this.vaccineReportService = vaccineReportService;
    }

    @GetMapping()
    //@PreAuthorize("hasRole('ADMIN')")
    public SearchVaccineReportResponse getListVaccineReport(
            @RequestParam(name = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNo,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
            @RequestParam(name = "origin",required = false) String origin,
            @RequestParam(name = "vaccineType",required = false)String vaccineTypeName
    ) {
        return vaccineReportService.getListVaccineReport(pageNo,pageSize,sortBy,sortDir,origin, vaccineTypeName);
    }

    /***
     * Thống kê tổng số lượng vaccine trong kho lọc theo năm và theo tên vaccine
     * @param year
     * @return
     */
    @GetMapping("graph")
    //@PreAuthorize("hasRole('ADMIN')")
    public Map<String, Integer> graphVaccineReport(@RequestParam int year,
                                                   @RequestParam(required = false, defaultValue = "") String vaccineName){
        return vaccineReportService.getVaccineGraphData(year, vaccineName);
    }

    @GetMapping("getAllYear")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public List<Integer> getAllyear(){
        return vaccineReportService.getAllYear();
    }
}
