package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.injection_schedule_request.InjectionScheduleRequest;
import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.dto.response.injection_schedule_response.SearchScheduleResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeSearchResponse;
import com.vaccination.BE.service.injectionschedule.InjectionScheduleService;
import com.vaccination.BE.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@Slf4j
public class InjectionScheduleController {
    InjectionScheduleService injectionScheduleService;

    public InjectionScheduleController(InjectionScheduleService injectionScheduleService) {
        this.injectionScheduleService = injectionScheduleService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchScheduleResponse getListSchedule(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId
    ) {

        return injectionScheduleService.getListSchedule(pageNo, pageSize, sortBy, sortDir, nameOrId);
    }
    @GetMapping("/open")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchScheduleResponse getListOpenSchedule(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId
    ) {

        return injectionScheduleService.getListOpenSchedule(pageNo, pageSize, sortBy, sortDir, nameOrId);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    InjectionScheduleResponse createInjectionSchedule(@RequestBody @Valid InjectionScheduleRequest request) {
        return injectionScheduleService.createInjectionSchedule(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    InjectionScheduleResponse updateInjectionSchedule(@PathVariable long id , @RequestBody @Valid InjectionScheduleRequest request)
    {
        return injectionScheduleService.updateInjectionSchedule(id,request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public InjectionScheduleResponse getInjectionScheduleById(@PathVariable long id) {
        return injectionScheduleService.getInjectionScheduleById(id);
    }
}
