package com.vaccination.BE.controller;


import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.VaccineTypeUpdateRequest;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeResponse;
import com.vaccination.BE.dto.response.vaccine_type_response.VaccineTypeSearchResponse;
import com.vaccination.BE.service.vaccinetype.VaccineTypeService;
import com.vaccination.BE.utils.AppConstants;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vaccinetype")
@Slf4j
public class VaccineTypeController {
    VaccineTypeService vaccineTypeService;

    public VaccineTypeController(VaccineTypeService vaccineTypeService) {
        this.vaccineTypeService = vaccineTypeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    VaccineTypeResponse createVaccineType(@ModelAttribute @Valid VaccineTypeRequest request) {
        request.setFile(request.getFile());
        return vaccineTypeService.createVaccineType(request, request.getFile());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    VaccineTypeResponse updateVaccineType(@PathVariable long id, @ModelAttribute @Valid VaccineTypeUpdateRequest request) {
        return vaccineTypeService.updateVaccineType(id, request, request.getFile());
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public VaccineTypeSearchResponse getListVaccineType(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId
    ) {

        return vaccineTypeService.getListVaccineType(pageNo, pageSize, sortBy, sortDir, nameOrId);
    }

//    @PostMapping("/checktype")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> checkVaccineType(@RequestBody long[] id) {
//        vaccineTypeService.makeVaccineTypesInactive(id);
//        return ResponseEntity.ok("Change status successfully!");
//    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeVaccineTypeStatus(@RequestBody List<StatusRequest> requests) {
        vaccineTypeService.changeVaccineTypeStatus(requests);
        return ResponseEntity.ok("Change status successfully!");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public VaccineTypeResponse getVaccineTypeById(@PathVariable long id) {
        return vaccineTypeService.getVaccineTypeById(id);
    }

    @GetMapping("/getAllId")
    @PreAuthorize("hasRole('ADMIN')")
    public long[] getAllVaccineTypeId(@RequestParam(name = "nameOrId", required = false) String nameOrId) {
        return vaccineTypeService.getAllVaccineTypeId(nameOrId);
    }
}
