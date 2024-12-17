package com.vaccination.BE.controller;


import com.vaccination.BE.dto.request.employee_request.EmailDto;
import com.vaccination.BE.dto.request.employee_request.EmployeeLoginRequest;
import com.vaccination.BE.dto.request.employee_request.EmployeeRequest;
import com.vaccination.BE.dto.request.employee_request.EmployeeUpdateRequest;
import com.vaccination.BE.dto.response.cloudinary_response.CloudinaryResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeLoginResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeSearchResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeUpdateResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.service.employee.EmployeeServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.vaccination.BE.utils.AppConstants;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/employee")
@Slf4j
public class UserController {

    EmployeeServiceImpl employeeService;

    public UserController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/login")
    EmployeeLoginResponse loginEmployee(@RequestBody @Valid EmployeeLoginRequest request) {
        return employeeService.login(request);
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeSearchResponse getListEmployee(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId
    ) {

        return employeeService.getListEmployee(pageNo, pageSize, sortBy, sortDir, nameOrId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(@PathVariable(name = "id") long id) {
        employeeService.deleteEmployeeById(id);
        return new ResponseEntity<>("Employee deleted successfully.", HttpStatus.OK);
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployees(@RequestBody long[] ids) {
        employeeService.deleteEmployeesById(ids);
        return new ResponseEntity<>("Employee deleted successfully.", HttpStatus.OK);
    }

//    @GetMapping("/employee")
//    @PreAuthorize("hasRole('ADMIN')")
//    public List<VaccineEmployee> getListEmployee(){
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        log.info("username :{}", authentication.getName());
//        authentication.getAuthorities().forEach(s -> log.info(s.getAuthority()));
//        return employeeService.getEmp();
//    }

    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse createEmployee(@ModelAttribute @Valid EmployeeRequest request) {
        request.setFile(request.getFile());
        return employeeService.createEmployee(request, request.getFile());
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeUpdateResponse> updateEmployee(@PathVariable long id, @ModelAttribute @Valid EmployeeUpdateRequest request) {
            return employeeService.updateEmployee(id, request, request.getFile());
    }

    @GetMapping("/{string}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse getEmployeeDetail(@PathVariable String string) {
        return employeeService.getEmployeeDetail(string);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestHeader String email) {
        return new ResponseEntity<>(employeeService.forgotPassword(email), HttpStatus.OK);
    }

    @PutMapping("/set-password")
    public ResponseEntity<String> setPassword(@RequestParam String token, @RequestHeader String newPassword) {
        if (employeeService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Password has been reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token.");
        }
    }

    @GetMapping("/check-token-isvalid")
    public boolean checkToken (@RequestParam String token)
    {
        return employeeService.checkTokenIsUsed(token);
    }
    @GetMapping("/getAllEmployee")
//    @PreAuthorize("hasRole('ADMIN')")
    public List<VaccineEmployee> getAllEmployee() {
        return employeeService.getAllEmployee();
    }


    @GetMapping("/getAllId")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Long> getAllEmployeeId(@RequestParam(name = "nameOrId", required = false) String nameOrId) {
        return employeeService.getAllEmployeeId(nameOrId);
    }

//    @PostMapping("/send-email")
//    public ResponseEntity<String> sendEmail(@RequestBody EmailDto emailDto){
//        return new ResponseEntity<>(employeeService.sendEmail(emailDto),HttpStatus.OK);
//    }
}
