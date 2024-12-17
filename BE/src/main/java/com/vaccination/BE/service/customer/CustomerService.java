package com.vaccination.BE.service.customer;

import com.vaccination.BE.dto.request.customer_request.CustomerRequest;
import com.vaccination.BE.dto.request.customer_request.CustomerUpdateRequest;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerUpdateResponse;
import com.vaccination.BE.dto.response.customer_response.SearchCustomerResponse;

public interface CustomerService {
    CustomerCreateResponse createCustomer(CustomerRequest customerRequest);
    SearchCustomerResponse getListCustomer(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
    CustomerUpdateResponse updateCustomer(Long id, CustomerUpdateRequest customerUpdateRequest);
    CustomerResponse getCustomerById(Long id);
    long [] getAllId(String nameOrId);
}
