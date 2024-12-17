package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.response.employee_response.EmployeeSearchResponse;
import com.vaccination.BE.dto.response.vaccine_response.SearchVaccineResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeSearchResponse;
import com.vaccination.BE.service.vaccine.VaccineService;
import com.vaccination.BE.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/vaccine")
@Slf4j
public class VaccineController {

    VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    VaccineResponse createVaccine(@RequestBody @Valid VaccineRequest request) {
        return vaccineService.createVaccine(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    VaccineResponse updateVaccine(@PathVariable long id , @RequestBody @Valid VaccineRequest request)
    {
        return vaccineService.updateVaccine(id,request);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        return vaccineService.uploadDataExcelFile(file);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchVaccineResponse getListVaccine(
            @RequestParam(name = "pageNo",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER,required = false) int pageNo,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.DEFAULT_PAGE_SIZE,required = false) int pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.DEFAULT_SORT_BY,required = false) String sortBy,
            @RequestParam(name = "sortDir",defaultValue = AppConstants.DEFAULT_SORT_DIRECTION,required = false) String sortDir,
            @RequestParam(name = "nameOrId",required = false) String nameOrId
    ) {

        return vaccineService.getListVaccine(pageNo,pageSize,sortBy,sortDir,nameOrId);
    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeVaccineStatus(@RequestBody List<StatusRequest> requests) {
        vaccineService.changeVaccineStatus(requests);
        return ResponseEntity.ok("Change status successfully!");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VaccineResponse getVaccineById(@PathVariable long id) {
        return vaccineService.getVaccineById(id);
    }

    @GetMapping("/getAllId")
    @PreAuthorize("hasRole('ADMIN')")
    public long[] getAllId(@RequestParam(name = "nameOrId", required = false) String nameOrId) {
        return vaccineService.getAllId(nameOrId);
    }
}
