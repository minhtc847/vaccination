package com.vaccination.BE.service.registerInject;

import com.vaccination.BE.dto.request.registerInject_request.RegisterInjectionRequest;
import com.vaccination.BE.dto.response.registerInject_response.InformationMailResponse;
import com.vaccination.BE.dto.response.registerInject_response.ListRegisterResponse;
import com.vaccination.BE.dto.response.registerInject_response.RegisterInjecctionResponse;
import com.vaccination.BE.entity.*;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
@Service
public class RegisterInjectImpl implements RegisterInjectService {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    VaccineRepository vaccineRepository;
    @Autowired
    InjectionScheduleRepository injectionScheduleRepository;
    @Autowired
    RegisterInjectRepository registerInjectRepository;
    @Autowired
    VaccineRuleRepository vaccineRuleRepository;
    @Autowired
    VaccineInjectionResultRepository vaccineInjectionResultRepository;

    @Override
    public ResponseEntity<RegisterInjecctionResponse> registerInject(RegisterInjectionRequest request) {


        RegisterInject registerInject = new RegisterInject();
        //lấy thông tin cus
        VaccineEmployee VaccineEmployee = employeeRepository.findByUsername(request.getUsername()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, "Not found customer")
        );
        //lấy thông tin lịch tiêm
        VaccineInjectionSchedule vaccineInjectionSchedule = injectionScheduleRepository.findById(request.getInjectionScheduleId()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, "a schedule not exist!")
        );
        System.out.println(vaccineInjectionSchedule.getVaccine().getId());
        //lấy thông tin vaccine
        VaccineVaccine vaccineVaccine = vaccineRepository.findById(vaccineInjectionSchedule.getVaccine().getId()).orElseThrow(
                () -> new APIException(HttpStatus.NOT_FOUND, "an vaccine not exist!")
        );
        //check register injection
        if (registerInjectRepository.findUser(request.getUsername(), request.getInjectionScheduleId(), vaccineInjectionSchedule.getStartDate(), vaccineInjectionSchedule.getEndDate()).isPresent()) {
            throw new APIException(HttpStatus.BAD_REQUEST, "You have registered");
        }

        //check ngày đó tiem du vaccine chưa
        if (registerInjectRepository.countByDay(request.getDate()) >= 50) {
            throw new APIException(HttpStatus.BAD_REQUEST, "The number of injections for the day has run out!!");
        } else {
            //check ngay
            if (request.getDate().isAfter(vaccineInjectionSchedule.getEndDate()) ||
                    request.getDate().isBefore(vaccineInjectionSchedule.getStartDate())) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Date must be between " + vaccineInjectionSchedule.getStartDate() + " to " + vaccineInjectionSchedule.getEndDate());
            } else {
                if (
                        !checkDateAvailable(request, VaccineEmployee, vaccineInjectionSchedule, vaccineVaccine)
                ) {
                    throw new APIException(HttpStatus.BAD_REQUEST, "the day not valid");
                }
                // Check contraindication
                List<String> ruleList = vaccineRuleRepository.getRuleById(vaccineVaccine.getId())
                        .stream()
                        .map(String::toLowerCase)
                        .toList();

                for (String rule : request.getContrain()) {
                    if (ruleList.contains(rule.toLowerCase())) {
                        throw new APIException(HttpStatus.BAD_REQUEST,
                                "You cannot get vaccinated because the vaccine contains contraindication " + rule);
                    }
                }
                registerInject.setVaccineInjectionSchedule(vaccineInjectionSchedule);
                registerInject.setDateInject(request.getDate());
                registerInject.setVaccineEmployee(VaccineEmployee);
                registerInject.setResult(false);
                registerInjectRepository.save(registerInject);
            }
        }
        RegisterInjecctionResponse response = new RegisterInjecctionResponse();
        response.setUsername(registerInject.getVaccineEmployee().getUsername());
        response.setVaccineName(registerInject.getVaccineInjectionSchedule().getVaccine().getVaccineName());
        response.setPlace(registerInject.getVaccineInjectionSchedule().getPlace());
        response.setDate(registerInject.getDateInject());
        return ResponseEntity.ok(response);
    }

    private boolean checkDateAvailable(RegisterInjectionRequest request, VaccineEmployee cus, VaccineInjectionSchedule injectionSchedule, VaccineVaccine vaccineVaccine) {
        boolean check = true;

//        if (!injectionSchedule.getStatus().equals("open") &&
//                !injectionSchedule.getStatus().equals("not yet")) {
//            check = false;
//        } else {
            // Check if customer has previous injection result
            if (vaccineInjectionResultRepository.existsByCustomerId(cus.getId())) {
                // If customer has previous injection results
                System.out.println("ID " + injectionSchedule.getVaccine().getId());
                List<VaccineInjectionResult> listInjectOfCus = vaccineInjectionResultRepository.findByCustomerId(cus.getId());

                // Check if customer has already received this vaccine
                boolean alreadyReceived = listInjectOfCus.stream()
                        .anyMatch(result -> Long.valueOf(result.getVaccine().getId()).equals(injectionSchedule.getVaccine().getId()));

                if (!alreadyReceived) {
                    // Calculate time difference in weeks
                    LocalDate dateInjectLatest = vaccineInjectionResultRepository.getLatestResults1(cus.getId());

                    long daysDiff = ChronoUnit.DAYS.between(dateInjectLatest, request.getDate());
                    long weeksDiff = daysDiff / 7;
                    // If more than 4 weeks
                    if (weeksDiff < 4) {
                        check = false;
                        throw new APIException(HttpStatus.BAD_REQUEST, "You must wait 4 weeks before receiving another vaccine!");

                    }
                } else {
                    // check injection times
                    int injectTimes = vaccineInjectionResultRepository.countByVaccineIdCustom(injectionSchedule.getVaccine().getId());
                    if (injectTimes == injectionSchedule.getInjectionTimes() - 1) {
                        // Get the latest injection date
                        LocalDate dateInjectLatest = vaccineInjectionResultRepository.getLatestResults(cus.getId(), vaccineVaccine.getId());

                        // Get vaccine details
                        Optional<VaccineVaccine> optionalVaccine = vaccineRepository.findById(injectionSchedule.getVaccine().getId());

                        if (optionalVaccine.isPresent()) {
                            VaccineVaccine vaccine = optionalVaccine.get();

                            // Calculate next injection date
                            LocalDate nextInjectionDate = dateInjectLatest.plusDays(vaccine.getTimeBeginNextInjection());
                            // Check conditions
                            if (injectionSchedule.getStartDate().isBefore(nextInjectionDate) &&
                                    injectionSchedule.getEndDate().isAfter(nextInjectionDate)) {
                                if (nextInjectionDate.isAfter(request.getDate())) {
                                    System.out.println(nextInjectionDate + "-" + dateInjectLatest);
                                    check = false;
                                    throw new APIException(HttpStatus.BAD_REQUEST, "You must wait " + vaccine.getTimeBeginNextInjection() + " before receiving vaccine!");
                                }
                            }
                        }

                    } else {
                        check = false;
                        throw new APIException(HttpStatus.BAD_REQUEST, "this is the Times " + injectionSchedule.getInjectionTimes() + " but you inject " + injectTimes);
                    }
                }
            }
//        }
        return check;
    }

    @Override
    public InformationMailResponse getInfor(String username, Long scheduleId) {
        InformationMailResponse informationMailResponse = new InformationMailResponse();
        informationMailResponse.setUsername(username);
        Optional<VaccineInjectionSchedule> vaccineInjectionSchedule = injectionScheduleRepository.findById(scheduleId);
        if (vaccineInjectionSchedule.isPresent()) {
            VaccineInjectionSchedule schedule = vaccineInjectionSchedule.get();
            informationMailResponse.setVaccineName(schedule.getVaccine().getVaccineName());
            informationMailResponse.setFrom(schedule.getStartDate());
            informationMailResponse.setEnd(schedule.getEndDate());
            informationMailResponse.setPlace(schedule.getPlace());
            informationMailResponse.setTimes(schedule.getInjectionTimes());
            informationMailResponse.setIndication(schedule.getVaccine().getIndication());
//
            Set<VaccineRule> getContrain = schedule.getVaccine().getContraindications();
            String[] rule = new String[getContrain.size()];
            int i = 0;
            for (VaccineRule v : getContrain) {
                rule[i] = v.getContraindication();
                i++;
            }
            informationMailResponse.setContrainditation(rule);
        }
        return informationMailResponse;
    }

    @Override
    public ListRegisterResponse getList(long cusId, LocalDate date, int page) {
        int size = 5; // fixed page size
        Pageable pageable = PageRequest.of(page, size);
        Page<RegisterInject> registerInjectPage;

        if (date != null) {
            registerInjectPage = registerInjectRepository.findByCusIdAndDate(cusId, date, pageable);
        } else {
            registerInjectPage = registerInjectRepository.findByCusId(cusId, pageable);
        }

        // Create ListRegisterResponse from Page<RegisterInject>
        List<RegisterInjecctionResponse> content = registerInjectPage.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        ListRegisterResponse response = new ListRegisterResponse();
        response.setContent(content);
        response.setPageNo(registerInjectPage.getNumber());
        response.setPageSize(registerInjectPage.getSize());
        response.setTotalElements(registerInjectPage.getTotalElements());
        response.setTotalPages(registerInjectPage.getTotalPages());
        response.setLast(registerInjectPage.isLast());

        return response;
    }

    private RegisterInjecctionResponse mapToResponse(RegisterInject registerInject) {
        RegisterInjecctionResponse response = new RegisterInjecctionResponse();
        response.setUsername(registerInject.getVaccineEmployee().getUsername());
        response.setVaccineName(registerInject.getVaccineInjectionSchedule().getVaccine().getVaccineName());
        response.setDate(registerInject.getDateInject());
        response.setPlace(registerInject.getVaccineInjectionSchedule().getPlace()); // Example mapping
        return response;
    }



}
