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
import com.vaccination.BE.entity.PasswordResetToken;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineRole;
import com.vaccination.BE.enums.Position;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.mapper.EmployeeMapper;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.PasswordResetTokenRepository;
import com.vaccination.BE.repository.RoleRepository;
import com.vaccination.BE.service.AuthenticationService;
import com.vaccination.BE.service.GeneratePassword;
import com.vaccination.BE.service.PasswordResetTokenService;
import com.vaccination.BE.service.cloudinary.CloudinaryServiceImpl;
import com.vaccination.BE.utils.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {
    EmployeeRepository employeeRepository;
    AuthenticationService authenticationService;
    EmployeeMapper employeeMapper;
    EmailUtil emailUtil;
    CloudinaryServiceImpl cloudinaryService;
    RoleRepository roleRepository;
    PasswordResetTokenService tokenService;
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public EmployeeLoginResponse login(EmployeeLoginRequest request) {

        var user = employeeRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("Sorry, your username or password is incorrect. Please try again!"));
        PasswordEncoder passwordEncode = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncode.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new RuntimeException("Sorry, your username or password is incorrect. Please try again!");
        }
        var token = authenticationService.generateToken(user);
        return EmployeeLoginResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public EmployeeSearchResponse getListEmployee(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        //Create Pageable object
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineEmployee> employees;
        if (nameOrId == null) {
            employees = employeeRepository.findByRoles(Position.ROLE_EMPLOYEE.toString(), pageRequest);
        } else if (nameOrId.matches("\\d+")) {
            employees = employeeRepository.findByIdContaining(nameOrId, Position.ROLE_EMPLOYEE.toString(), pageRequest);
        } else {
            employees = employeeRepository.findByEmployeeNameContaining(nameOrId, Position.ROLE_EMPLOYEE.toString(), pageRequest);
        }
        List<VaccineEmployee> VaccineEmployees = employees.getContent();
        //Map to List<EmployeeResponse>
        List<EmployeeResponse> content = VaccineEmployees.stream().map(employee -> {
                    EmployeeResponse employeeResponse = new EmployeeResponse();
                    employeeMapper.employeeToEmployeeResponse(employeeResponse, employee);
                    employeeResponse.setEmployeeId(employee.getId());
                    return employeeResponse;
                }
        ).collect(Collectors.toList());

        return EmployeeSearchResponse.builder()
                .totalElements(employees.getTotalElements())
                .content(content)
                .last(employees.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(employees.getTotalPages())
                .build();
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request, MultipartFile file) {
        if (employeeRepository.existsByUsername(request.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        if (employeeRepository.existsByPhone(request.getPhone())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Phone number already exists");
        }
        if (request.getEmployeeName().matches(".*[^a-zA-Z\\s].*")) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Employee must be contain only characters");
        }
        VaccineEmployee employee = employeeMapper.toEmployee(request);
        employee.setUsername(request.getUsername());
        String randomPassword = GeneratePassword.generatePassword();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        employee.setPassword(passwordEncoder.encode(randomPassword));

        EmailDto emailDto = new EmailDto();
        emailDto.setTo(request.getEmail());
        emailDto.setSubject("Information Account");
        emailDto.setContent("");
        emailDto.putProp("name", request.getEmployeeName());
        emailDto.putProp("username", request.getUsername());
        emailDto.putProp("password", randomPassword);
        sendEmail(emailDto);
        if (file != null && !file.isEmpty()) {
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file);
            employee.setImage(cloudinaryResponse.getUrl());
        }
        VaccineRole role = roleRepository.findByName("ROLE_EMPLOYEE")
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Role not found"));
        Set<VaccineRole> set = new HashSet<>();
        set.add(role);
        employee.setRoles(set);
        employee.setPosition(request.getPosition());
        employee.setVersion(0L);
        employeeRepository.save(employee);

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setEmployeeId(employee.getId());
        employeeMapper.employeeToEmployeeResponse(employeeResponse, employee);
        return employeeResponse;
    }


    @Override
    public void deleteEmployeeById(long id) {
        VaccineEmployee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", String.valueOf(id)));
        cloudinaryService.deleteImage(employee.getImage());
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public void deleteEmployeesById(long[] ids) {
        for (long id : ids) {
            VaccineEmployee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", String.valueOf(id)));

            try {
                employeeRepository.delete(employee);
                employeeRepository.flush();
                cloudinaryService.deleteImage(employee.getImage());
            }catch (DataIntegrityViolationException e) {
                if(e.getMessage().contains("register_inject")) throw new APIException(HttpStatus.CONFLICT, "Customer %s is already registered a vaccine schedule".formatted(employee.getEmployeeName()));
            }
        }
    }

    @Override
    @Transactional
    public ResponseEntity<EmployeeUpdateResponse> updateEmployee(long id, EmployeeUpdateRequest request, MultipartFile file) {
        if (request.getEmployeeName().matches(".*[^a-zA-Z\\s].*")) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Employee must be contain only characters");
        }
        VaccineEmployee VaccineEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", String.valueOf(id)));
        //System.out.println("VaccineEmployee version 1: " + VaccineEmployee.getVersion());
        //System.out.println("Request version 1: " + request.getVersion());
        // Kiểm tra phiên bản trước khi cập nhật
        if (!Objects.equals(request.getVersion(), VaccineEmployee.getVersion())) {
            throw new APIException(HttpStatus.CONFLICT, "Conflict detected: The record was updated by another user.");
        }

        // Kiểm tra email và số điện thoại đã tồn tại
        if (!request.getEmail().equals(VaccineEmployee.getEmail())) {
            if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Email existed!");
            }
        }
        if (!request.getPhone().equals(VaccineEmployee.getPhone())) {
            if (employeeRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Phone existed!");
            }
        }

        // Xử lý ảnh
        if (VaccineEmployee.getImage() != null) {
            cloudinaryService.deleteImage(VaccineEmployee.getImage());
        }
        employeeMapper.mmployeeUpdateRequestupdateEmployee(VaccineEmployee, request);

        if (file != null && !file.isEmpty()) {
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadImage(file);
            VaccineEmployee.setImage(cloudinaryResponse.getUrl());
        }
        VaccineRole role = roleRepository.findByName(request.getPosition())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Role not found"));
        Set<VaccineRole> set = new HashSet<>();
        set.add(role);
        VaccineEmployee.setRoles(set);
        VaccineEmployee.setPosition(request.getPosition());
        // In ra giá trị phiên bản để kiểm tra
        //System.out.println("Request version: " + request.getVersion());
        //System.out.println("VaccineEmployee version: " + VaccineEmployee.getVersion());


        // Cập nhật thông tin và lưu đối tượng
        try {
            VaccineEmployee.setVersion(VaccineEmployee.getVersion() + 1); // Cập nhật phiên bản mới
            VaccineEmployee updatedEmployee = employeeRepository.save(VaccineEmployee);
            EmployeeUpdateResponse employeeUpdateResponse = new EmployeeUpdateResponse();
            employeeMapper.employeeToEmployeeUpdateResponse(employeeUpdateResponse, updatedEmployee);
            employeeUpdateResponse.setEmployeeId(id);

            return ResponseEntity.ok(employeeUpdateResponse);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new APIException(HttpStatus.CONFLICT, "Conflict detected: The record was updated by another user.");
        }
    }


    @Override
    public EmployeeResponse getEmployeeDetail(String string) {
        VaccineEmployee VaccineEmployee;
        EmployeeResponse employeeResponse = new EmployeeResponse();
        if (string.matches("\\d+")) {
            // Nếu string chỉ toàn số, tìm bằng ID
            long id = Long.parseLong(string);
            employeeResponse.setEmployeeId(id);
            VaccineEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", string));
        } else {
            // Nếu string chứa chữ, tìm bằng username
            VaccineEmployee = employeeRepository.findByUsername(string)
                    .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "username", string));
            employeeResponse.setEmployeeId(VaccineEmployee.getId());
        }
        employeeMapper.employeeToEmployeeResponse(employeeResponse, VaccineEmployee);
        employeeResponse.setVersion(VaccineEmployee.getVersion());
        return employeeResponse;

    }

    public String forgotPassword(String email) {
        employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email!"));
        try {
            emailUtil.sendSetPassword(email);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email, please try again.");
        }
        return "Please check your email to set a new password";
    }

    public boolean resetPassword(String token, String newPassword) {
        if (tokenService.validateToken(token)) {
            System.out.println("Check mail");
            String email = tokenService.getEmailByToken(token);
            if (email != null) {
                System.out.println("Ket qua mail dung " + setPassword(email, newPassword).equals("Set password successfully!"));
                return setPassword(email, newPassword).equals("Set password successfully!");
            }
        }
        return false;
    }

    @Override
    public String setPassword(String email, String newPassword) {
        VaccineEmployee VaccineEmployee = employeeRepository.findByEmail(email)
                .orElseThrow(
                        () -> new APIException(HttpStatus.NOT_FOUND, "User not found with this email!")
                );
        System.out.println("co user ");
        if (!isValidPassword(newPassword)) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Password must contain at least one uppercase letter, one lowercase letter, and one digit");
        }
        System.out.println("Pass ok");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        VaccineEmployee.setPassword(passwordEncoder.encode(newPassword));
        employeeRepository.save(VaccineEmployee);
        return "Set password successfully!";
    }

    @Override
    public List<VaccineEmployee> getAllEmployee() {
        return employeeRepository.findByRoleId("ROLE_EMPLOYEE");
    }

    @Override
    public List<Long> getAllEmployeeId(String nameOrId) {
        List<VaccineEmployee> list = employeeRepository.findByRoleId(Position.ROLE_EMPLOYEE.toString());
        List<Long> employeeIds;
        if (nameOrId == null) {
            employeeIds = list.stream()
                    .map(VaccineEmployee::getId)
                    .collect(Collectors.toList());
        } else if (nameOrId.matches("\\d+")) {
            employeeIds = list.stream()
                    .filter(employee -> Long.toString(employee.getId()).contains(nameOrId.toLowerCase()))
                    .map(VaccineEmployee::getId)
                    .collect(Collectors.toList());
        } else {
            employeeIds = list.stream()
                    .filter(employee -> employee.getEmployeeName().toLowerCase().contains(nameOrId.toLowerCase()))
                    .map(VaccineEmployee::getId)
                    .collect(Collectors.toList());
        }
        return employeeIds;
    }

    private boolean isValidPassword(String password) {
        System.out.println("pass" + password);
        // Check if password contains at least one uppercase letter, one lowercase letter, and one digit
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$";
        return password.matches(passwordPattern);
    }

    @Override
    public String sendEmail(EmailDto emailDto) {
        try {
            emailUtil.sendEmail(emailDto);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email,pls try again");
        }
        return "Please check your email ";
    }

    public boolean checkTokenIsUsed(String token) {
        Optional<PasswordResetToken> passwordResetTokenOpt = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenOpt.isPresent()) {
            PasswordResetToken passwordResetToken = passwordResetTokenOpt.get();
            if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                passwordResetToken.setUsed(true);
                passwordResetTokenRepository.save(passwordResetToken);
                System.out.println("1" + passwordResetToken.isUsed());
            }
            System.out.println("2" + passwordResetToken.isUsed());
            return passwordResetToken.isUsed();
        }

        return false;
    }


//    public List<VaccineEmployee> getEmp() {
//        return employeeRepository.findAll();
//    }
}
