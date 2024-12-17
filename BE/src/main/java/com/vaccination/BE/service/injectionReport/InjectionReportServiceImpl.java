package com.vaccination.BE.service.injectionReport;

import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.dto.response.injection_report_response.InjectionReportResponse;
import com.vaccination.BE.entity.VaccineInjectionResult;
import com.vaccination.BE.mapper.*;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.VaccineInjectionResultRepository;
import com.vaccination.BE.repository.VaccineTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InjectionReportServiceImpl implements InjectionReportService {
    VaccineInjectionResultRepository vaccineInjectionResultRepository;
    VaccineInjectionResultMapper vaccineInjectionResultMapper;

    EmployeeRepository employeeRepository;
    EmployeeMapper employeeMapper;

    VaccineTypeRepository vaccineTypeRepository;
    VaccineTypeMapper vaccineTypeMapper;

    InjectionReportMapper injectionReportMapper;

    public InjectionReportServiceImpl(VaccineInjectionResultRepository vaccineInjectionResultRepository, VaccineInjectionResultMapper vaccineInjectionResultMapper, EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, VaccineTypeRepository vaccineTypeRepository, VaccineTypeMapper vaccineTypeMapper, InjectionReportMapper injectionReportMapper) {
        this.vaccineInjectionResultRepository = vaccineInjectionResultRepository;
        this.vaccineInjectionResultMapper = vaccineInjectionResultMapper;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.vaccineTypeMapper = vaccineTypeMapper;
        this.injectionReportMapper = injectionReportMapper;
    }

    @Override
    public SearchInjectionReportResponse getListInjectionReport(int pageNo, int pageSize, String sortBy, String sortDir, String usage, LocalDate injectDateFrom, LocalDate injectDateTo, String vaccineTypeName) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineInjectionResult> page;

        if (usage == null) {
            if (vaccineTypeName == null) {
                if (injectDateFrom != null || injectDateTo != null) {
                    if (injectDateFrom != null && injectDateTo != null) {
                        page = vaccineInjectionResultRepository.findByInjectDateBetween(injectDateFrom, injectDateTo, pageRequest);
                    } else if (injectDateFrom != null) {
                        page = vaccineInjectionResultRepository.findByInjectDateFrom(injectDateFrom, pageRequest);
                    } else {
                        page = vaccineInjectionResultRepository.findByInjectDateTo(injectDateTo, pageRequest);
                    }
                } else {
                    page = vaccineInjectionResultRepository.findAllWithVaccineTypeTrue(pageRequest);
                }
            } else {
                if (injectDateFrom != null || injectDateTo != null) {
                    if (injectDateFrom != null && injectDateTo != null) {
                        page = vaccineInjectionResultRepository.findByVaccineTypeNameAndInjectDateBetween(vaccineTypeName, injectDateFrom, injectDateTo, pageRequest);
                    } else if (injectDateFrom != null) {
                        page = vaccineInjectionResultRepository.findByVaccineTypeNameAndInjectDateFrom(vaccineTypeName, injectDateFrom, pageRequest);
                    } else {
                        page = vaccineInjectionResultRepository.findByVaccineTypeNameAndInjectDateTo(vaccineTypeName, injectDateTo, pageRequest);
                    }
                } else {
                    page = vaccineInjectionResultRepository.findByVaccineTypeName(vaccineTypeName, pageRequest);
                }
            }

        } else {
            if (vaccineTypeName == null) {
                if (injectDateFrom != null || injectDateTo != null) {
                    if (injectDateFrom != null && injectDateTo != null) {
                        page = vaccineInjectionResultRepository.findByUsageAndInjectDateBetween(usage, injectDateFrom, injectDateTo, pageRequest);
                    } else if (injectDateFrom != null) {
                        page = vaccineInjectionResultRepository.findByUsageAndInjectDateFrom(usage, injectDateFrom, pageRequest);
                    } else {
                        page = vaccineInjectionResultRepository.findByUsageAndInjectDateTo(usage, injectDateTo, pageRequest);
                    }
                } else {

                    page = vaccineInjectionResultRepository.findAllWithVaccineUsage(usage, pageRequest);
                }
            } else {
                if (injectDateFrom != null || injectDateTo != null) {
                    if (injectDateFrom != null && injectDateTo != null) {
                        page = vaccineInjectionResultRepository.findByUsageAndVaccineTypeNameAndInjectDateBetween(usage, vaccineTypeName, injectDateFrom, injectDateTo, pageRequest);
                    } else if (injectDateFrom != null) {
                        page = vaccineInjectionResultRepository.findByUsageAndVaccineTypeNameAndInjectDateFrom(usage, vaccineTypeName, injectDateFrom, pageRequest);
                    } else {
                        page = vaccineInjectionResultRepository.findByUsageAndVaccineTypeNameAndInjectDateTo(usage, vaccineTypeName, injectDateTo, pageRequest);
                    }
                } else {
                    page = vaccineInjectionResultRepository.findAllWithVaccineUsageAndVaccineTypeName(usage, vaccineTypeName, pageRequest);
                }
            }

        }

        List<VaccineInjectionResult> vaccineResults = page.getContent();
        List<InjectionReportResponse> content = vaccineResults.stream().map(result -> {
            String employeeName = result.getCustomer().getEmployeeName();
            return injectionReportMapper.toVaccineReportResponse(result);
        }).collect(Collectors.toList());


        SearchInjectionReportResponse searchReportResponse = SearchInjectionReportResponse.builder()
                .totalElements(page.getTotalElements())
                .content(content)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();
        return searchReportResponse;
    }

    @Override
    public Map<String, Integer> getInjectionGraphData(int year) {
        Map<String, Integer> data = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            Integer total = vaccineInjectionResultRepository.totalInjectionByMonthAndYear(month, year);
            if (total == null) {
                total = 0;
            }
            data.put(getMonthName(month, year), total);
        }
        return data;
    }

    @Override
    public List<Integer> getAllYear() {
        return vaccineInjectionResultRepository.findAllYears();
    }

    private static String getMonthName(int month, int year) {
        String monthName = YearMonth.of(year, month).getMonth().name().toLowerCase();
        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
    }
}
