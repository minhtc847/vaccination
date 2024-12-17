package com.vaccination.BE.mapper;

import com.vaccination.BE.dto.request.customer_request.CustomerRequest;
import com.vaccination.BE.dto.request.customer_request.CustomerUpdateRequest;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerUpdateResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "address", source = "address") // Map Address to address
    @Mapping(target = "image", ignore = true) // Ignore image if not provided
    @Mapping(target = "password", ignore = true) // Ignore password if not updating it
    @Mapping(target = "position", ignore = true) // Ignore position if not provided
    @Mapping(target = "workingPlace", ignore = true) // Ignore workingPlace if not provided
    @Mapping(target = "roles", ignore = true) // Ignore roles if not provided
    @Mapping(target = "version", ignore = true) // Ignore version if not provided
    void customerToEmployee(@MappingTarget VaccineEmployee VaccineEmployee , CustomerRequest customerRequest);

    @Mapping(target = "address", source = "address") // Map Address to address
    @Mapping(target = "email", ignore = true) // Ignore image if not provided
    @Mapping(target = "phone", ignore = true) // Ignore password if not provided
//    @Mapping(target = "position", source = "address") // Ignore position if not provided
//    @Mapping(target = "workingPlace",source = "address") // Ignore workingPlace if not provided
//    @Mapping(target = "roles", ignore = true) // Ignore roles if not provided
    @Mapping(target = "version", ignore = true) // Map version if provided
    void CustoCustomerCreateResponse(@MappingTarget CustomerCreateResponse customerCreateResponse, CustomerRequest customerRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", source = "address") // Map Address to address
    @Mapping(target = "image", ignore = true) // Ignore image if not provided
    @Mapping(target = "password", ignore = true) // Ignore password if not provided
    @Mapping(target = "position", ignore = true) // Ignore position if not provided
    @Mapping(target = "workingPlace", ignore = true) // Ignore workingPlace if not provided
    @Mapping(target = "roles", ignore = true) // Ignore roles if not provided
    @Mapping(target = "version", source = "version") // Map version if provided
    void CustomerUpdateReqToEmpl(@MappingTarget VaccineEmployee VaccineEmployee, CustomerUpdateRequest customerUpdateRequest);
    void CustomerUpdateResponse(@MappingTarget CustomerUpdateResponse customerUpdateResponse,CustomerUpdateRequest customerUpdateRequest);

    void employeeToCustomerResponse(@MappingTarget CustomerResponse customerResponse, VaccineEmployee VaccineEmployee);
}
