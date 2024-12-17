package com.vaccination.BE.service.customer_report;

import com.vaccination.BE.dto.response.customer_report_response.CustomerReportResponse;
import com.vaccination.BE.dto.response.customer_report_response.SearchCustomerReportResponse;
import com.vaccination.BE.dto.response.injection_report_response.InjectionReportResponse;
import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.entity.VaccineInjectionResult;
import com.vaccination.BE.mapper.EmployeeMapper;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.VaccineInjectionResultRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerReportServiceImpl implements CustomerReportService {

    EmployeeMapper employeeMapper;
    EmployeeRepository employeeRepository;
    VaccineInjectionResultRepository vaccineInjectionResultRepository;

    public CustomerReportServiceImpl(EmployeeMapper employeeMapper, EmployeeRepository employeeRepository, VaccineInjectionResultRepository vaccineInjectionResultRepository) {
        this.employeeMapper = employeeMapper;
        this.employeeRepository = employeeRepository;
        this.vaccineInjectionResultRepository = vaccineInjectionResultRepository;
    }

    @Override
    public SearchCustomerReportResponse getListCustomerReport(int pageNo, int pageSize,
                                                              String sortBy, String sortDir,
                                                              LocalDate dateFrom, LocalDate dateTo,
                                                              String fullName, String address) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineEmployee> page = null;

        if (fullName == null) {
            if (address == null) {
                if (dateFrom != null || dateTo != null) {
                    if (dateFrom != null && dateTo != null) {
                        page = employeeRepository.findByDateFromAndDateTo(dateFrom, dateTo, pageRequest);
                    } else if (dateFrom != null) {
                        page = employeeRepository.findByDateFrom(dateFrom, pageRequest);
                    } else {
                        page = employeeRepository.findByDateTo(dateTo, pageRequest);
                    }
                } else {
                    page = employeeRepository.findAllCustomerWithRoleCustomer(3, pageRequest);
                }
            } else {
                if (dateFrom != null || dateTo != null) {
                    if (dateFrom != null && dateTo != null) {
                        page = employeeRepository.findByDateFromAndDateToAndAddress(dateFrom, dateTo, address, pageRequest);
                    } else if (dateFrom != null) {
                        page = employeeRepository.findByDateFromAndAddress(dateFrom, address, pageRequest);
                    } else {
                        page = employeeRepository.findByDateToAndAddress(dateTo, address, pageRequest);
                    }
                } else {
                    page = employeeRepository.findAllCustomerAndAdress(address, pageRequest);
                }
            }
        } else {
            if (address == null) {
                if (dateFrom != null || dateTo != null) {
                    if (dateFrom != null && dateTo != null) {
                        page = employeeRepository.findByDateFromAndDateToAndFullName(dateFrom, dateTo, fullName, pageRequest);
                    } else if (dateFrom != null) {
                        page = employeeRepository.findByDateFromAndFullName(dateFrom, fullName, pageRequest);
                    } else {
                        page = employeeRepository.findByDateToAndFullName(dateTo, fullName, pageRequest);
                    }
                } else {
                    page = employeeRepository.findAllCustomerAndFullName(fullName, pageRequest);
                }
            } else {
                if (dateFrom != null || dateTo != null) {
                    if (dateFrom != null && dateTo != null) {
                        page = employeeRepository.findByDateFromAndDateToAndFullNameAndAddress(dateFrom, dateTo, fullName, address, pageRequest);
                    } else if (dateFrom != null) {
                        page = employeeRepository.findByDateFromAndFullNameAndAddress(dateFrom, fullName, address, pageRequest);
                    } else {
                        page = employeeRepository.findByDateToAndFullNameAndAddress(dateTo, fullName, address, pageRequest);
                    }
                } else {
                    page = employeeRepository.findAllCustomerAndFullNameAndAddress(fullName, address, pageRequest);
                }
            }
        }

        List<CustomerReportResponse> content = page.getContent().stream()
                .map(vaccineEmployee -> convertVaccineEmployeeToCustomerReportResponse(vaccineEmployee, getNumberOfInject(vaccineEmployee)))
                .collect(Collectors.toList());

        SearchCustomerReportResponse searchReportResponse = SearchCustomerReportResponse.builder()
                .totalElements(page.getTotalElements())
                .content(content)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();

        return searchReportResponse;
    }

    private int getNumberOfInject(VaccineEmployee vaccineEmployee) {
        return vaccineInjectionResultRepository.countByCustomerId(vaccineEmployee.getId());
    }

    private CustomerReportResponse convertVaccineEmployeeToCustomerReportResponse(VaccineEmployee vaccineEmployee, int numberOfInject) {
        return CustomerReportResponse.builder()
                .employeeName(vaccineEmployee.getEmployeeName())
                .dateOfBirth(vaccineEmployee.getDateOfBirth())
                .address(vaccineEmployee.getAddress())
                .identityCard(vaccineEmployee.getIdentityCard())
                .numberOfInject(numberOfInject)
                .build();
    }


}
