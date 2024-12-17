package com.vaccination.BE.service.vaccine;

import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.request.vaccine_type_request.StatusRequest;
import com.vaccination.BE.dto.response.vaccine_response.SearchVaccineResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.entity.VaccineInjectionSchedule;
import com.vaccination.BE.entity.VaccineRule;
import com.vaccination.BE.entity.VaccineVaccine;
import com.vaccination.BE.entity.VaccineVaccineType;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.mapper.VaccineMapper;
import com.vaccination.BE.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import com.vaccination.BE.utils.ExcelUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.io.IOException;


@Service
public class VaccineServiceImpl implements VaccineService {

    VaccineRepository vaccineRepository;
    VaccineMapper vaccineMapper;
    VaccineTypeRepository vaccineTypeRepository;
    VaccineRuleRepository vaccineRuleRepository;

    InjectionScheduleRepository injectionScheduleRepository;

    VaccineInjectionResultRepository vaccineInjectionResultRepository;
    RegisterInjectRepository registerInjectRepository;

    public VaccineServiceImpl(VaccineRepository vaccineRepository,
                              VaccineMapper vaccineMapper,
                              VaccineTypeRepository vaccineTypeRepository,
                              VaccineRuleRepository vaccineRuleRepository,
                              RegisterInjectRepository registerInjectRepository,
                              VaccineInjectionResultRepository vaccineInjectionResultRepository,
                              InjectionScheduleRepository injectionScheduleRepository) {
        this.vaccineRepository = vaccineRepository;
        this.vaccineMapper = vaccineMapper;
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.vaccineRuleRepository = vaccineRuleRepository;
        this.registerInjectRepository = registerInjectRepository;
        this.vaccineInjectionResultRepository = vaccineInjectionResultRepository;
        this.injectionScheduleRepository = injectionScheduleRepository;
    }

    @Override
    public VaccineResponse createVaccine(VaccineRequest request) {
        VaccineVaccine vaccine = vaccineMapper.toVaccine(request);
        VaccineVaccineType vaccineType = vaccineTypeRepository.findById(request.getVaccineTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine type", "id", String.valueOf(request.getVaccineTypeId())));
        vaccine.setVaccineType(vaccineType);

        // Save to database
        try {
            vaccineRepository.save(vaccine);

            // Response
            VaccineResponse vaccineResponse = new VaccineResponse();
            vaccineMapper.vaccineToVaccineResponse(vaccineResponse, vaccine);
            vaccineResponse.setStatus(true);
            vaccineResponse.setVaccineTypeName(vaccineType.getVaccineTypeName());
            String[] contraindications = request.getContraindication();
            vaccineResponse.setContraindication(contraindications);

            List<VaccineRule> vaccineRules = new ArrayList<>();
            for (String contraindication : contraindications) {
                VaccineRule vaccineRule = new VaccineRule();
                vaccineRule.setVaccine(vaccine);
                vaccineRule.setContraindication(contraindication);
                vaccineRules.add(vaccineRule);
            }
            vaccineRuleRepository.saveAll(vaccineRules);

            return vaccineResponse;
        } catch (DataIntegrityViolationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Vaccine name is existed");
        }
    }

    @Override
    public VaccineResponse updateVaccine(long id, VaccineRequest request) {
        VaccineVaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine", "id", String.valueOf(id)));

        VaccineVaccineType vaccineType = vaccineTypeRepository.findById(request.getVaccineTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine type", "id", String.valueOf(request.getVaccineTypeId())));

        vaccineMapper.updateVaccine(vaccine, request);

        try {
            vaccineRepository.save(vaccine);
        } catch (DataIntegrityViolationException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Vaccine name is already existed!");
        }

        VaccineResponse vaccineResponse = new VaccineResponse();
        vaccineMapper.vaccineToVaccineResponse(vaccineResponse, vaccine);
        vaccineResponse.setId(vaccine.getId());
        vaccineResponse.setVaccineTypeId(vaccineType.getId());
        String[] contraindications = request.getContraindication();
        vaccineResponse.setContraindication(contraindications);

        // Update existing VaccineRule objects
        List<VaccineRule> existingVaccineRules = vaccineRuleRepository.findByVaccineId(vaccine.getId());
        for (VaccineRule existingRule : existingVaccineRules) {
            boolean found = false;
            for (String contraindication : contraindications) {
                if (existingRule.getContraindication().equals(contraindication)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                vaccineRuleRepository.delete(existingRule);
            }
        }

        // Add new VaccineRule objects
        for (String contraindication : contraindications) {
            boolean exists = existingVaccineRules.stream()
                    .anyMatch(rule -> rule.getContraindication().equals(contraindication));
            if (!exists) {
                VaccineRule newRule = new VaccineRule();
                newRule.setVaccine(vaccine);
                newRule.setContraindication(contraindication);
                vaccineRuleRepository.save(newRule);
            }
        }

        return vaccineResponse;
    }


    public SearchVaccineResponse getListVaccine(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineVaccine> page;
        if (nameOrId == null) {
            page = vaccineRepository.findAllWithVaccineTypeTrue(pageRequest);
        } else if (nameOrId.matches("\\d+")) {
            page = vaccineRepository.findByIdContaining(nameOrId, pageRequest);
        } else {
            page = vaccineRepository.findByVaccineNameContaining(nameOrId, pageRequest);
        }
        List<VaccineVaccine> vaccineVaccines = page.getContent();
        List<VaccineResponse> content = vaccineVaccines.stream().map(vaccine -> {
                    VaccineResponse vaccineResponse = new VaccineResponse();
                    vaccineMapper.vaccineToVaccineResponse(vaccineResponse, vaccine);
                    vaccineResponse.setVaccineTypeName(vaccine.getVaccineType().getVaccineTypeName());
                    List<VaccineRule> vaccineRules = vaccineRuleRepository.findByVaccineId(vaccine.getId());
                    if (!vaccineRules.isEmpty()) {
                        String[] contrains = vaccineRules.stream().map(VaccineRule::getContraindication).toArray(String[]::new);
                        vaccineResponse.setContraindication(contrains);
                    }
                    return vaccineResponse;
                }
        ).collect(Collectors.toList());
        SearchVaccineResponse searchResponse = SearchVaccineResponse.builder()
                .totalElements(page.getTotalElements())
                .content(content)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();
        return searchResponse;
    }

    @Override
    @Transactional
    public void changeVaccineStatus(List<StatusRequest> requests) {
        for (StatusRequest request : requests) {
            VaccineVaccine vaccine = vaccineRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Vaccine", "id", String.valueOf(request.getId()))
                    );
            vaccine.setStatus(false);
            vaccineRepository.save(vaccine);
        }
    }

    @Override
    public ResponseEntity<String> uploadDataExcelFile(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a file");
        }

        try {
            List<VaccineVaccine> vaccineVaccineList = ExcelUtil.excelToData(file);
            vaccineRepository.saveAll(vaccineVaccineList);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully and data saved to database");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process the file: " + e.getMessage());
        }
    }

    @Override
    public VaccineResponse getVaccineById(long id) {
        VaccineVaccine vaccine = vaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine", "id", String.valueOf(id)));
//        String[] contraindications = vaccineRuleRepository.findByVaccineId(vaccine.getId()).stream().map(VaccineRule::getContraindication).toArray(String[]::new);
        String[] contraindications = vaccine.getContraindications().stream().map(VaccineRule::getContraindication).toArray(String[]::new);
        VaccineResponse response = new VaccineResponse();
        vaccineMapper.vaccineToVaccineResponse(response, vaccine);
        response.setContraindication(contraindications);
        return response;
    }

    @Override
    public long[] getAllId(String nameOrId) {
        List<VaccineVaccine> list = vaccineRepository.findAllActiveById();
        long[] ids;
        if (nameOrId == null) {
            ids = list.stream().mapToLong(vaccine -> vaccine.getId()).toArray();
        } else if (nameOrId.matches("\\d+")) {
            ids = list.stream().filter(vaccine -> Long.toString(vaccine.getId()).contains(nameOrId)).mapToLong(vaccine -> vaccine.getId()).toArray();
        } else {
            ids = list.stream().filter(vaccine -> vaccine.getVaccineName().toLowerCase().contains(nameOrId.toLowerCase())).mapToLong(vaccine -> vaccine.getId()).toArray();
        }
        return ids;
    }

    private String determineConstraintViolationMessage(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();
        if (message.contains("vaccine_vaccine")) {
            return "Vaccine name is existed";
        } else if (message.contains("vaccine_rule")) {
            return "Contraindication is existed";
        } else {
            return "Data integrity violation";
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkVaccine() {
        for (VaccineVaccine vaccine : vaccineRepository.findAll()) {
            try {
                int vaccineResult = vaccineInjectionResultRepository.countByDay(LocalDate.now(), vaccine.getId());
                int InjectPerDay = injectionScheduleRepository.getByVaccineId(vaccine.getId());
                System.out.println(vaccine.getVaccineName() + ":" + "InjectPerDay " + InjectPerDay + " vaccineResult " + vaccineResult);
                System.out.println("------------");
                if (InjectPerDay > vaccineResult) {
                    Optional<VaccineVaccine> optionalVaccineVaccine = vaccineRepository.findById(vaccine.getId());
                    if (optionalVaccineVaccine.isPresent()) {
                        VaccineVaccine vaccineVaccine = optionalVaccineVaccine.get();
                        vaccineVaccine.setTotalInject(vaccineVaccine.getTotalInject() + InjectPerDay - vaccineResult);
                    } else {
                        System.out.println("Vaccine not found");
                    }
                }
            } catch (Exception e) {
            }
        }

    }
}
