package com.vaccination.BE.service.employee;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest request, MultipartFile file);

    EmployeeLoginResponse login(EmployeeLoginRequest request);

    EmployeeSearchResponse getListEmployee(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);

    void deleteEmployeeById(long id);

    void deleteEmployeesById(long[] ids);


    EmployeeResponse getEmployeeDetail(String string);

    String forgotPassword(String email);

    String setPassword(String email, String newPassword);

    List<VaccineEmployee> getAllEmployee();

    List<Long> getAllEmployeeId(String nameOrId);

    String sendEmail(EmailDto emailDto);
    ResponseEntity<EmployeeUpdateResponse> updateEmployee(long id, EmployeeUpdateRequest request, MultipartFile file) ;
}
