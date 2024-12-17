package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.customer_request.CustomerRequest;
import com.vaccination.BE.dto.request.customer_request.CustomerUpdateRequest;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.customer_response.SearchCustomerResponse;
import com.vaccination.BE.dto.response.employee_response.EmployeeSearchResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerUpdateResponse;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.service.customer.CustomerService;
import com.vaccination.BE.service.customer.CustomerServiceImpl;
import com.vaccination.BE.utils.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerController {
@Autowired
    CustomerService customerService;
    @PostMapping("/create-customer")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public CustomerCreateResponse createCustomer(@RequestBody   CustomerRequest customerRequest) {
        return customerService.createCustomer(customerRequest);
    }


    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public SearchCustomerResponse getListCustomer(
            @RequestParam(name = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(name = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir,
            @RequestParam(name = "nameOrId", required = false) String nameOrId
    ) {

        return customerService.getListCustomer(pageNo, pageSize, sortBy, sortDir, nameOrId);
    }

     @PutMapping("/update-customer/{id}")
     @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
    public CustomerUpdateResponse updateCustomer(@PathVariable Long id,@RequestBody CustomerUpdateRequest customerUpdateRequest)
     {
         return customerService.updateCustomer(id,customerUpdateRequest);
     }

     @GetMapping("{id}")
     @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
     public CustomerResponse getCustomerById(@PathVariable Long id)
     {
         return customerService.getCustomerById(id);
     }

     @GetMapping("/getAllId")
     @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_EMPLOYEE')")
     public long[] getAllId(@RequestParam(name = "nameOrId", required = false) String nameOrId)
     {
         return customerService.getAllId(nameOrId);
     }

}
