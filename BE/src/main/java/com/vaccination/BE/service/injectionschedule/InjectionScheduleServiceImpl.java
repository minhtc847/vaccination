package com.vaccination.BE.service.injectionschedule;


import com.vaccination.BE.dto.request.employee_request.EmailDto;
import com.vaccination.BE.dto.request.injection_schedule_request.InjectionScheduleRequest;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.dto.response.injection_schedule_response.SearchScheduleResponse;
import com.vaccination.BE.dto.response.vaccine_response.SearchVaccineResponse;
import com.vaccination.BE.dto.response.vaccine_response.VaccineResponse;
import com.vaccination.BE.entity.*;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.mapper.InjectionScheduleMapper;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.InjectionScheduleRepository;
import com.vaccination.BE.repository.VaccineInjectionResultRepository;
import com.vaccination.BE.repository.VaccineRepository;
import com.vaccination.BE.utils.EmailUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InjectionScheduleServiceImpl implements InjectionScheduleService {

    InjectionScheduleRepository injectionScheduleRepository;

    InjectionScheduleMapper injectionScheduleMapper;
    VaccineRepository vaccineRepository;
    EmployeeRepository employeeRepository;

    VaccineInjectionResultRepository vaccineInjectionResultRepository;
    @Autowired
    EmailUtil emailUtil;

    public InjectionScheduleServiceImpl(InjectionScheduleRepository injectionScheduleRepository
            , InjectionScheduleMapper injectionScheduleMapper
            , VaccineRepository vaccineRepository
            , EmployeeRepository employeeRepository
            , VaccineInjectionResultRepository vaccineInjectionResultRepository) {
        this.injectionScheduleRepository = injectionScheduleRepository;
        this.injectionScheduleMapper = injectionScheduleMapper;
        this.vaccineRepository = vaccineRepository;
        this.employeeRepository = employeeRepository;
        this.vaccineInjectionResultRepository = vaccineInjectionResultRepository;
    }

    @Override
    public SearchScheduleResponse getListSchedule(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineInjectionSchedule> page;
        if (nameOrId == null) {
            page = injectionScheduleRepository.findAllScheduleTrue(pageRequest,true);
        } else {
            page = injectionScheduleRepository.findByVaccineNameContaining(nameOrId, pageRequest,true);
        }
        List<VaccineInjectionSchedule> injectionSchedules = page.getContent();
        List<InjectionScheduleResponse> content = injectionSchedules.stream().map(injectionSchedule -> {
                    InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
                    injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, injectionSchedule);
                    injectionScheduleResponse.setVaccineId(injectionSchedule.getVaccine().getId());
                    injectionScheduleResponse.setVaccineName(injectionSchedule.getVaccine().getVaccineName());
                    injectionScheduleResponse.setStatus(getStatusFromStartAndEndDate(injectionSchedule.getStartDate(), injectionSchedule.getEndDate()));
                    return injectionScheduleResponse;
                }
        ).collect(Collectors.toList());
        SearchScheduleResponse searchResponse = SearchScheduleResponse.builder()
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
    public InjectionScheduleResponse createInjectionSchedule(InjectionScheduleRequest request) {
        Optional<VaccineVaccine> vaccineOptional = vaccineRepository.findById(request.getVaccineId());
        if (vaccineOptional.isPresent()) {
            VaccineVaccine vaccine = vaccineOptional.get();
            int injectPerDay = request.getInjectPerDay();
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();

            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            int maxAllowedInjections = injectPerDay * (int) daysBetween;

            if (injectPerDay <= 0 || maxAllowedInjections > vaccine.getTotalInject()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Vaccine is not enough. Please enter Injection Per Day again.");
            }
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "From date must be less than to date");
        }

        VaccineInjectionSchedule injectionSchedule = injectionScheduleMapper.toInjectionSchedule(request);
        VaccineVaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new ResourceNotFoundException(" vaccine", "id", String.valueOf(request.getVaccineId())));
        injectionSchedule.setVaccine(vaccine);
        // Determine and set the status
//        LocalDate now = LocalDate.now();
//        if (now.isBefore(request.getStartDate())) {
//            injectionSchedule.setStatus("not yet");
//        } else if (now.isAfter(request.getEndDate())) {
//            injectionSchedule.setStatus("over");
//        } else {
//            injectionSchedule.setStatus("open");
//        }
        injectionSchedule.setInjectionTimes(request.getInjectionTimes());
        // Save schedule to database
        injectionScheduleRepository.save(injectionSchedule);

        // Process injection schedule
        processInjectionSchedule(injectionSchedule);
        // Prepare and return response
        InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
        injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, injectionSchedule);
        injectionScheduleResponse.setId(injectionSchedule.getId());
        injectionScheduleResponse.setVaccineId(injectionSchedule.getVaccine().getId());
        injectionScheduleResponse.setInjectionTimes(injectionSchedule.getInjectionTimes());
        if (vaccineOptional.isPresent()) {
            VaccineVaccine vaccine1 = vaccineOptional.get();

            int injectPerDay = request.getInjectPerDay();
            LocalDate startDate = request.getStartDate();
            LocalDate endDate = request.getEndDate();

            long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
            int maxAllowedInjections = injectPerDay * (int) daysBetween + injectPerDay;


            vaccine1.setTotalInject(vaccine.getTotalInject() - maxAllowedInjections);
            vaccineRepository.save(vaccine1);
        }
        return injectionScheduleResponse;

    }

    private void processInjectionSchedule(VaccineInjectionSchedule injectionSchedule) {
//        if (injectionSchedule.getStatus().equals("open") || injectionSchedule.getStatus().equals("not yet")) {
        List<VaccineEmployee> listCus = employeeRepository.findByRoleId("ROLE_CUSTOMER");

        for (VaccineEmployee cus : listCus) {
            // Check if customer has previous injection result
            if (!vaccineInjectionResultRepository.existsByCustomerId(cus.getId())) {
                // If customer doesn't have result => send email
                sendScheduleEmail(cus, injectionSchedule);
            } else {
                // If customer has previous injection results
                List<VaccineInjectionResult> listInjectOfCus = vaccineInjectionResultRepository.findByCustomerId(cus.getId());

                // Check if customer has already received this vaccine
                boolean alreadyReceived = listInjectOfCus.stream()
                        .anyMatch(result -> Long.valueOf(result.getVaccine().getId()).equals(injectionSchedule.getVaccine().getId()));

                if (!alreadyReceived) {
                    // Calculate time difference in weeks
                    LocalDate dateInjectLatest = vaccineInjectionResultRepository.getLatestResults1(cus.getId());
                    long daysDiff = ChronoUnit.DAYS.between(dateInjectLatest, injectionSchedule.getStartDate());
                    long weeksDiff = daysDiff / 7;
                    // If more than 4 weeks => send email
                    if (weeksDiff >= 4) {
                        sendScheduleEmail(cus, injectionSchedule);
                    }
                } else {
                    // check injection times
                    int injectTimes = vaccineInjectionResultRepository.countByVaccineIdCustom(injectionSchedule.getVaccine().getId());
                    if (injectTimes == injectionSchedule.getInjectionTimes() - 1) {
                        // Get the latest injection date
                        LocalDate dateInjectLatest = vaccineInjectionResultRepository.getLatestResults(cus.getId(), injectionSchedule.getVaccine().getId());

                        // Get vaccine details
                        Optional<VaccineVaccine> optionalVaccine = vaccineRepository.findById(injectionSchedule.getVaccine().getId());

                        if (optionalVaccine.isPresent()) {
                            VaccineVaccine vaccineVaccine = optionalVaccine.get();
                            // Calculate next injection date
                            LocalDate nextInjectionDate = dateInjectLatest.plusDays(vaccineVaccine.getTimeBeginNextInjection());
                            // Check conditions for sending email
                            if (injectionSchedule.getStartDate().isAfter(nextInjectionDate) ||
                                    (injectionSchedule.getStartDate().isBefore(nextInjectionDate) &&
                                            injectionSchedule.getEndDate().isAfter(nextInjectionDate))) {
                                sendScheduleEmail(cus, injectionSchedule);
                            }
                        }
                    }
                }
            }
        }
    }

    private void sendScheduleEmail(VaccineEmployee cus, VaccineInjectionSchedule injectionSchedule) {
        EmailDto emailDto = new EmailDto();
        emailDto.setTo(cus.getEmail());
        emailDto.setSubject("Vaccine Schedule");
        emailDto.setContent("");
        emailDto.putProp("cus", cus);
        emailDto.putProp("schedule", injectionSchedule);
        sendEmail(emailDto);
    }


    public String getStatusFromStartAndEndDate(LocalDate startDate, LocalDate endDate) {
        if ((startDate.isBefore(LocalDate.now()) || startDate.isEqual(LocalDate.now())) && (endDate.isAfter(LocalDate.now()) || endDate.isEqual(LocalDate.now()))) {
            return "Open";
        }
        if (startDate.isAfter(LocalDate.now())) {
            return "Not yet";
        } else {
            return "Over";
        }
    }

    @Override
    public InjectionScheduleResponse updateInjectionSchedule(long id, InjectionScheduleRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new APIException(HttpStatus.BAD_REQUEST, "From date must be less than to date");
        }
        VaccineInjectionSchedule injectionSchedule = injectionScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Injection Schedule", "id", String.valueOf(id)));

        VaccineVaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine", "id", String.valueOf(request.getVaccineId())));

        injectionScheduleMapper.updateInjectionSchedule(injectionSchedule, request);
        injectionScheduleRepository.save(injectionSchedule);
        InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
        injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, injectionSchedule);
        injectionScheduleResponse.setId(injectionSchedule.getId());
        injectionScheduleResponse.setVaccineId(vaccine.getId());
        return injectionScheduleResponse;
    }

    @Override
    public InjectionScheduleResponse getInjectionScheduleById(long id) {
        VaccineInjectionSchedule injectionSchedule = injectionScheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Injection Schedule", "id", String.valueOf(id)));
        InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
        injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, injectionSchedule);
        injectionScheduleResponse.setVaccineId(injectionSchedule.getVaccine().getId());
        injectionScheduleResponse.setStatus(getStatusFromStartAndEndDate(injectionSchedule.getStartDate(), injectionSchedule.getEndDate()));
        return injectionScheduleResponse;
    }

    @Override
    public SearchScheduleResponse getListOpenSchedule(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineInjectionSchedule> page;
        if (nameOrId == null) {
            page = injectionScheduleRepository.findAllScheduleTrueAndOpen(pageRequest,true);
        } else {
            page = injectionScheduleRepository.findByVaccineNameContainingAndOpen(nameOrId, pageRequest,true);
        }
        List<VaccineInjectionSchedule> injectionSchedules = page.getContent();
        List<InjectionScheduleResponse> content = injectionSchedules.stream().map(injectionSchedule -> {
                    InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
                    injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, injectionSchedule);
                    injectionScheduleResponse.setVaccineId(injectionSchedule.getVaccine().getId());
                    injectionScheduleResponse.setVaccineName(injectionSchedule.getVaccine().getVaccineName());
                    injectionScheduleResponse.setStatus(getStatusFromStartAndEndDate(injectionSchedule.getStartDate(), injectionSchedule.getEndDate()));
                    return injectionScheduleResponse;
                }
        ).collect(Collectors.toList());
        SearchScheduleResponse searchResponse = SearchScheduleResponse.builder()
                .totalElements(page.getTotalElements())
                .content(content)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();
        return searchResponse;
    }

    public void sendEmail(EmailDto emailDto) {
        emailUtil.sendEmailRegisterInject(emailDto);
    }
}
