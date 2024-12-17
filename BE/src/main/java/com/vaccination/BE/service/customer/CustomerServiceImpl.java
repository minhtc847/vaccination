package com.vaccination.BE.service.customer;

import com.vaccination.BE.dto.request.customer_request.CustomerRequest;
import com.vaccination.BE.dto.request.customer_request.CustomerUpdateRequest;
import com.vaccination.BE.dto.request.employee_request.EmailDto;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.customer_response.SearchCustomerResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerUpdateResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineRole;
import com.vaccination.BE.enums.Position;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.mapper.CustomerMapper;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.RoleRepository;
import com.vaccination.BE.utils.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    RoleRepository roleRepository;
    CustomerMapper customerMapper;
    @Autowired
    EmailUtil emailUtil;

    @Override
    public CustomerCreateResponse createCustomer(CustomerRequest customerRequest) {
        if (employeeRepository.existsByIdentityCard(customerRequest.getIdentityCard())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Identity Card is exist");
        }

        if (employeeRepository.existsByUsername(customerRequest.getUsername())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (employeeRepository.existsByEmail(customerRequest.getEmail())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        if (employeeRepository.existsByPhone(customerRequest.getPhone())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Phone number already exists");
        }
        if (!customerRequest.getIdentityCard().matches("\\d{12}")) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Identity Card must be exactly 12 digits");
        }
        if (!customerRequest.getPassword().equals(customerRequest.getPasswordConfirm())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Password Confirm not match");
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        VaccineEmployee VaccineEmployee = new VaccineEmployee();
        customerMapper.customerToEmployee(VaccineEmployee, customerRequest);
        VaccineEmployee.setPassword(passwordEncoder.encode(customerRequest.getPassword()));

        EmailDto emailDto = new EmailDto();
        emailDto.setTo(customerRequest.getEmail());
        emailDto.setSubject("Information Account");
        emailDto.setContent("");
        emailDto.putProp("name", customerRequest.getEmployeeName());
        emailDto.putProp("username", customerRequest.getUsername());
        emailDto.putProp("password", customerRequest.getPassword());
        sendEmail(emailDto);
        VaccineEmployee.setEmployeeName(customerRequest.getEmployeeName());
        VaccineEmployee.setIdentityCard(customerRequest.getIdentityCard());
        VaccineEmployee.setVersion(0L);
        VaccineRole role = roleRepository.findByName("ROLE_CUSTOMER")
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Role not found"));
        VaccineEmployee.setRoles(Set.of(role));
        VaccineEmployee.setPosition("ROLE_CUSTOMER");
        VaccineEmployee.setVersion(0L);
        employeeRepository.save(VaccineEmployee);
        CustomerCreateResponse customerCreateResponse = new CustomerCreateResponse();
        customerMapper.CustoCustomerCreateResponse(customerCreateResponse, customerRequest);
        customerCreateResponse.setAddress(customerRequest.getAddress());
        return customerCreateResponse;
    }

    @Override
    public SearchCustomerResponse getListCustomer(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineEmployee> customers;
        if (nameOrId == null) {
            customers = employeeRepository.findByRoles(Position.ROLE_CUSTOMER.toString(), pageRequest);
        } else if (nameOrId.matches("\\d+")) {
            customers = employeeRepository.findByIdContaining(nameOrId, Position.ROLE_CUSTOMER.toString(), pageRequest);
        } else {
            customers = employeeRepository.findByEmployeeNameContaining(nameOrId, Position.ROLE_CUSTOMER.toString(), pageRequest);
        }
        List<VaccineEmployee> VaccineEmployees = customers.getContent();
        //Map to List<EmployeeResponse>
        List<CustomerResponse> content = VaccineEmployees.stream().map(customer -> {
                    CustomerResponse customerResponse = new CustomerResponse();
                    customerMapper.employeeToCustomerResponse(customerResponse, customer);
                    customerResponse.setId(customer.getId());
                    return customerResponse;
                }
        ).collect(Collectors.toList());

        return SearchCustomerResponse.builder()
                .totalElements(customers.getTotalElements())
                .content(content)
                .last(customers.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(customers.getTotalPages())
                .build();
    }

    @Override
    public CustomerUpdateResponse updateCustomer(Long id, CustomerUpdateRequest customerUpdateRequest) {
        VaccineEmployee VaccineEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", String.valueOf(id)));
        System.out.println("VaccineEmployee version 1: " + VaccineEmployee.getVersion());
        System.out.println("Request version 1: " + customerUpdateRequest.getVersion());
        // Kiểm tra phiên bản trước khi cập nhật
        if (!Objects.equals(customerUpdateRequest.getVersion(), VaccineEmployee.getVersion())) {
            throw new APIException(HttpStatus.CONFLICT, "Conflict detected: The record was updated by another user.");
        }

        if (!customerUpdateRequest.getIdentityCard().equals(VaccineEmployee.getIdentityCard())) {
            if (employeeRepository.existsByIdentityCard(customerUpdateRequest.getIdentityCard())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Identity Card is exist");
            }
        }
        if (!customerUpdateRequest.getUsername().equals(VaccineEmployee.getUsername())) {
            if (employeeRepository.existsByUsername(customerUpdateRequest.getUsername())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Username already exists");
            }
        }
        if (!customerUpdateRequest.getEmail().equals(VaccineEmployee.getEmail())) {
            if (employeeRepository.findByEmail(customerUpdateRequest.getEmail()).isPresent()) {
//            throw new RuntimeException("Email existed!");
                throw new APIException(HttpStatus.BAD_REQUEST, "Email existed!");
            }
        }
        if (!customerUpdateRequest.getPhone().equals(VaccineEmployee.getPhone())) {
            if (employeeRepository.findByPhone(customerUpdateRequest.getPhone()).isPresent()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Phone existed!");
            }
        }
        if (!customerUpdateRequest.getIdentityCard().matches("\\d{12}")) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Identity Card must be exactly 12 digits");
        }
//    if(!customerUpdateRequest.getPassword().equals(customerUpdateRequest.getPasswordConfirm())) {
//        throw new APIException(HttpStatus.BAD_REQUEST, "Password Confirm not match");
//    }

        try {
            customerMapper.CustomerUpdateReqToEmpl(VaccineEmployee, customerUpdateRequest);
            VaccineEmployee.setAddress(customerUpdateRequest.getAddress());
            VaccineEmployee.setEmployeeName(customerUpdateRequest.getEmployeeName());
            VaccineEmployee.setIdentityCard(customerUpdateRequest.getIdentityCard());
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
//    VaccineEmployee.setPassword(passwordEncoder.encode(customerUpdateRequest.getPassword()));
            VaccineEmployee.setVersion(VaccineEmployee.getVersion() + 1);
            employeeRepository.save(VaccineEmployee);
            CustomerUpdateResponse customerUpdateResponse = new CustomerUpdateResponse();
            customerUpdateResponse.setAddress(customerUpdateRequest.getAddress());
            customerMapper.CustomerUpdateResponse(customerUpdateResponse, customerUpdateRequest);
            return customerUpdateResponse;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new APIException(HttpStatus.CONFLICT, "Conflict detected: The record was updated by another user.");
        }
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        VaccineEmployee VaccineEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("VaccineEmployee", "id", String.valueOf(id)));
        CustomerResponse customerResponse = new CustomerResponse();
        customerMapper.employeeToCustomerResponse(customerResponse, VaccineEmployee);
        customerResponse.setVersion(VaccineEmployee.getVersion());
        return customerResponse;
    }

    @Override
    public long[] getAllId(String nameOrId) {
        List<VaccineEmployee> customers = employeeRepository.findByRoleId(Position.ROLE_CUSTOMER.toString());
        long[] customerIds;
        if(nameOrId ==null){
            customerIds = customers.stream()
                    .mapToLong(VaccineEmployee::getId)
                    .toArray();
        }else if (nameOrId.matches("\\d+")) {
            customerIds = customers.stream()
                    .filter(employee -> Long.toString(employee.getId()).contains(nameOrId))
                    .mapToLong(VaccineEmployee::getId)
                    .toArray();
        } else{
            customerIds = customers.stream()
                    .filter(employee -> employee.getEmployeeName().toLowerCase().contains(nameOrId.toLowerCase()))
                    .mapToLong(VaccineEmployee::getId)
                    .toArray();
        }
        return customerIds;
    }

    public void sendEmail(EmailDto emailDto) {
        try {
            emailUtil.sendEmail(emailDto);
        } catch (MessagingException e) {
            throw new RuntimeException("Unable to send set password email,pls try again");
        }
    }

}

