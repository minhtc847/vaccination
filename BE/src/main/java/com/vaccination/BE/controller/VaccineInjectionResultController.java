package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultForCreateRequest;
import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultRequest;
import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.dto.response.news_response.SearchNewsResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.InjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.SearchVaccineInjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.ResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.VaccineInjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.service.vaccineInjectionResult.VaccineInjectionResultService;
import com.vaccination.BE.utils.AppConstants;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/result")
public class VaccineInjectionResultController {
    VaccineInjectionResultService vaccineInjectionResultService;

    public VaccineInjectionResultController(VaccineInjectionResultService vaccineInjectionResultService) {
        this.vaccineInjectionResultService = vaccineInjectionResultService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    String createVaccineInjectionResult(
        @RequestBody VaccineInjectionResultForCreateRequest request) {
        return vaccineInjectionResultService.createVaccineInjectionResult(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    VaccineInjectionResultResponse updateVaccineInjectionResult(@PathVariable long id , @RequestBody @Valid VaccineInjectionResultRequest request)
    {
        return vaccineInjectionResultService.updateVaccineInjectionResult(id,request);
    }
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchVaccineInjectionResultResponse getAllNews(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId){
        return vaccineInjectionResultService.getAllInjectionResult(pageNo,pageSize,sortBy,sortDir,nameOrId);
    }
    @GetMapping("/getAllId")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public long[] getAllId(@RequestParam(name = "nameOrId", required = false) String nameOrId) {
        return vaccineInjectionResultService.getAllId(nameOrId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public InjectionResultResponse getVaccineInjectionResultById(@PathVariable long id) {
        return vaccineInjectionResultService.getVaccineInjectionResultById(id);
    }
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public String deleteVaccineInjectionResults(@RequestBody long[] ids) {
        vaccineInjectionResultService.deleteVaccineInjectionResults(ids);
        return "Vaccine injection result deleted successfully";
    }
    @GetMapping("/inject/{scheduleId}")
    ResultResponse getInforFromScheduleId(@PathVariable long scheduleId)
    {
        return vaccineInjectionResultService.getInforFromScheduleId(scheduleId);
    }
}
