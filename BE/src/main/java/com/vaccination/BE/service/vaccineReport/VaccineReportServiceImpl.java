package com.vaccination.BE.service.vaccineReport;

import com.vaccination.BE.dto.response.injection_report_response.InjectionReportResponse;
import com.vaccination.BE.dto.response.injection_report_response.SearchInjectionReportResponse;
import com.vaccination.BE.dto.response.vaccine_report_response.SearchVaccineReportResponse;
import com.vaccination.BE.dto.response.vaccine_report_response.VaccineReportResponse;
import com.vaccination.BE.entity.VaccineInjectionResult;
import com.vaccination.BE.entity.VaccineVaccine;
import com.vaccination.BE.mapper.VaccineMapper;
import com.vaccination.BE.mapper.VaccineReportMapper;
import com.vaccination.BE.repository.VaccineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VaccineReportServiceImpl implements VaccineReportService {

    VaccineMapper vaccineMapper;
    VaccineRepository vaccineRepository;
    VaccineReportMapper vaccineReportMapper;

    public VaccineReportServiceImpl(VaccineMapper vaccineMapper, VaccineRepository vaccineRepository, VaccineReportMapper vaccineReportMapper) {
        this.vaccineMapper = vaccineMapper;
        this.vaccineRepository = vaccineRepository;
        this.vaccineReportMapper = vaccineReportMapper;
    }

    @Override
    public SearchVaccineReportResponse getListVaccineReport(int pageNo, int pageSize,
                                                            String sortBy, String sortDir,
                                                            String origin,
                                                            String vaccineTypeName) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineVaccine> page;

        if (origin == null) {
            if(vaccineTypeName == null) {
                page = vaccineRepository.findAllWithVaccineTypeTrue(pageRequest);
            }else{
                page = vaccineRepository.findByVaccineTypeName(vaccineTypeName, pageRequest);
            }
        } else {
            if (vaccineTypeName == null) {
                page = vaccineRepository.findByOriginContaining(origin, pageRequest);
            } else {
                page = vaccineRepository.findByVaccineTypeNameAndOrigin(vaccineTypeName, origin, pageRequest);
            }
        }

        List<VaccineVaccine> vaccineResults = page.getContent();
        List<VaccineReportResponse> content = vaccineResults.stream().map(result -> {
            return vaccineReportMapper.toVaccineReportResponse(result);
        }).collect(Collectors.toList());


        SearchVaccineReportResponse searchReportResponse = SearchVaccineReportResponse.builder()
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
    public Map<String, Integer> getVaccineGraphData(int year, String vaccineName) {
        Map<String, Integer> data = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
           Integer total = vaccineRepository.totalInjectionByDateAndVaccineName(month, year, vaccineName);
            if (total == null) {
                total = 0;
            }
            data.put(getMonthName(month, year), total);
        }
        return data;
    }

    private static String getMonthName(int month, int year) {
        String monthName = YearMonth.of(year, month).getMonth().name().toLowerCase();
        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
    }

    @Override
    public List<Integer> getAllYear() {
        return vaccineRepository.findAllYears();
    }
}
