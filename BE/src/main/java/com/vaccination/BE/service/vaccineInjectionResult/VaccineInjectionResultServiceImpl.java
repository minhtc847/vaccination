package com.vaccination.BE.service.vaccineInjectionResult;

import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultForCreateRequest;
import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultRequest;
import com.vaccination.BE.dto.response.customer_response.CustomerCreateResponse;
import com.vaccination.BE.dto.response.customer_response.CustomerResponse;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.InjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.SearchVaccineInjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.ResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.VaccineInjectionResultResponse;
import com.vaccination.BE.entity.*;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.mapper.CustomerMapper;
import com.vaccination.BE.mapper.InjectionScheduleMapper;
import com.vaccination.BE.mapper.VaccineInjectionResultMapper;
import com.vaccination.BE.repository.EmployeeRepository;
import com.vaccination.BE.repository.VaccineInjectionResultRepository;
import com.vaccination.BE.repository.VaccineRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.vaccination.BE.repository.*;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VaccineInjectionResultServiceImpl implements VaccineInjectionResultService {
    VaccineInjectionResultRepository vaccineInjectionResultRepository;
    VaccineInjectionResultMapper vaccineInjectionResultMapper;
    VaccineRepository vaccineRepository;
    EmployeeRepository employeeRepository;
    CustomerMapper customerMapper;
    InjectionScheduleMapper injectionScheduleMapper;
    RegisterInjectRepository registerInjectRepository;
    InjectionScheduleRepository injectionScheduleRepository;

    public VaccineInjectionResultServiceImpl(VaccineInjectionResultRepository vaccineInjectionResultRepository,
                                             VaccineInjectionResultMapper vaccineInjectionResultMapper,
                                             VaccineRepository vaccineRepository,
                                             EmployeeRepository employeeRepository,
                                             CustomerMapper customerMapper,
                                             InjectionScheduleMapper injectionScheduleMapper,
                                             RegisterInjectRepository registerInjectRepository,
                                             InjectionScheduleRepository injectionScheduleRepository) {

        this.vaccineInjectionResultRepository = vaccineInjectionResultRepository;
        this.vaccineInjectionResultMapper = vaccineInjectionResultMapper;
        this.vaccineRepository = vaccineRepository;
        this.employeeRepository = employeeRepository;
        this.customerMapper = customerMapper;
        this.injectionScheduleMapper = injectionScheduleMapper;
        this.registerInjectRepository = registerInjectRepository;
        this.injectionScheduleRepository =injectionScheduleRepository;
    }

    @Override
    public String createVaccineInjectionResult(VaccineInjectionResultForCreateRequest request) {
        VaccineInjectionResultResponse response = new VaccineInjectionResultResponse();

        for (String cus : request.getCustomerName()) {
            // Log customer name
            System.out.println("Cus: " + cus);

            // Find vaccine employee by username
            VaccineEmployee VaccineEmployee = employeeRepository.findByUsername(cus)
                    .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Not found Customer"));

            // Find vaccine injection schedule by ID
            VaccineInjectionSchedule vaccineInjectionSchedule = injectionScheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Not found Schedule"));

            // Find register inject by customer name and schedule ID
            RegisterInject registerInject = registerInjectRepository.findByCusNameAndScheduleId(cus, request.getScheduleId())
                    .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "Not found registerInject"));

            // Change status to true and save register inject
            registerInject.setResult(true);
            registerInjectRepository.save(registerInject);

            // Create and save vaccine injection result
            VaccineInjectionResult vaccineInjectionResult = new VaccineInjectionResult();
            vaccineInjectionResult.setInjectionDate(LocalDate.now());
            vaccineInjectionResult.setInjectionSchedule(vaccineInjectionSchedule);
            vaccineInjectionResult.setVaccine(vaccineInjectionSchedule.getVaccine());
            vaccineInjectionResult.setCustomer(VaccineEmployee);
            vaccineInjectionResultRepository.save(vaccineInjectionResult);

            // Set response fields
            response.setCustomerId(VaccineEmployee.getId());
            response.setInjectionDate(LocalDate.now());
            response.setResult("true");
            response.setPlace(vaccineInjectionSchedule.getPlace());
            response.setScheduleId(vaccineInjectionSchedule.getId());
            response.setVaccineId(vaccineInjectionSchedule.getVaccine().getId());
        }

        return "Update Succesful!";
    }


    @Override
    public VaccineInjectionResultResponse updateVaccineInjectionResult(long id, VaccineInjectionResultRequest request) {
//        if (request.getInjectionDate() != null) {
//            if (request.getInjectionDate().isBefore(LocalDate.now())) {
//                throw new APIException(HttpStatus.BAD_REQUEST,
//                        "Please input Date of vaccination with value greater or equal the current date");
//            }
//        }

        VaccineInjectionResult vaccineInjectionResult = vaccineInjectionResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine injection result", "id", String.valueOf(id)));


        VaccineVaccine vaccine = injectionScheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, "VaccineVaccine not found with ID: " + request.getScheduleId())).getVaccine();
        vaccineInjectionResult.setVaccine(vaccine);


        VaccineEmployee VaccineEmployee = employeeRepository.findByUsernameAndPosition(request.getCustomerName(), "CUSTOMER")
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerName()));

        vaccineInjectionResult.setCustomer(VaccineEmployee);

        boolean isCustomer = VaccineEmployee.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_CUSTOMER"));
        if (!isCustomer) {
            throw new ResourceNotFoundException("Customer", "id", String.valueOf(request.getCustomerName()));
        }
//        RegisterInject registerInject = registerInjectRepository.findByCusIdAndScheduleId(request.getCustomerId(),request.getScheduleId());
//        if(registerInject == null)
//        {
//            throw new APIException(HttpStatus.NOT_FOUND,"Not Found Customer with schedule! ");
//        }else{
//            registerInject.setResult();
//            registerInjectRepository.save(registerInject);
//        }
        vaccineInjectionResultMapper.updateVaccineInjectionResult(vaccineInjectionResult, request);
        vaccineInjectionResultRepository.save(vaccineInjectionResult);
        VaccineInjectionResultResponse vaccineInjectionResultResponse = new VaccineInjectionResultResponse();
        vaccineInjectionResultMapper.vaccineInjectionResultToVaccineInjectionResultResponse(vaccineInjectionResultResponse, vaccineInjectionResult);
        vaccineInjectionResultResponse.setVaccineId(vaccine.getId());
        vaccineInjectionResultResponse.setCustomerId(VaccineEmployee.getId());
        return vaccineInjectionResultResponse;
    }

    @Override
    public SearchVaccineInjectionResultResponse getAllInjectionResult(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageRequest = PageRequest.of(pageNo, pageSize, sort);
        Page<VaccineInjectionResult> page;
        if (nameOrId == null) {
            page = vaccineInjectionResultRepository.findAllWithVaccineTypeTrue(pageRequest);
        } else {
            page = vaccineInjectionResultRepository.findByCustomerNameContaining(pageRequest, nameOrId);
        }

        List<VaccineInjectionResult> vaccineInjectionResults = page.getContent();
        List<InjectionResultResponse> vaccineInjectionResultResponses = vaccineInjectionResults.stream().map(result->{
            InjectionResultResponse response = new InjectionResultResponse();
            vaccineInjectionResultMapper.vaccineInjectionResultToInjectionResultResponse(response, result);

            //add days for next injection
            VaccineVaccine resultVaccine = result.getVaccine();
            int injectTime = result.getInjectionSchedule().getInjectionTimes();
            int lastInjectTime = resultVaccine.getNumberOfInjection();
            if(injectTime == lastInjectTime){
                response.setNextInjectionDate(null);
            }else{
                response.setNextInjectionDate(result.getInjectionDate().plusDays(resultVaccine.getTimeBeginNextInjection()));
            }

            //add customer
            CustomerResponse customerResponse = new CustomerResponse();
            customerMapper.employeeToCustomerResponse(customerResponse, result.getCustomer());
            response.setCustomer(customerResponse);
            //add injection schedule
            InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
            injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, result.getInjectionSchedule());
            response.setInjectionSchedule(injectionScheduleResponse);
            response.setInjectionTime(result.getInjectionSchedule().getInjectionTimes());

            //add vaccine type
            response.getVaccine().setVaccineTypeId(resultVaccine.getVaccineType().getId());
            response.getVaccine().setVaccineTypeName(resultVaccine.getVaccineType().getVaccineTypeName());

            return response;
        }).collect(Collectors.toList());

        SearchVaccineInjectionResultResponse searchVaccineInjectionResultResponse = SearchVaccineInjectionResultResponse.builder()
                .totalElements(page.getTotalElements())
                .content(vaccineInjectionResultResponses)
                .last(page.isLast())
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPaged(page.getTotalPages())
                .build();
        return searchVaccineInjectionResultResponse;
    }

    @Override
    public long[] getAllId(String nameOrId) {
        return nameOrId != null ? vaccineInjectionResultRepository.getAllId(nameOrId) : vaccineInjectionResultRepository.getAllId();
    }

    @Override
    public InjectionResultResponse getVaccineInjectionResultById(long id) {
        VaccineInjectionResult vaccineInjectionResult = vaccineInjectionResultRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vaccine injection result","id",String.valueOf(id)));
        InjectionResultResponse response = new InjectionResultResponse();
        vaccineInjectionResultMapper.vaccineInjectionResultToInjectionResultResponse(response, vaccineInjectionResult);

        //add days for next injection
        VaccineVaccine resultVaccine = vaccineInjectionResult.getVaccine();
        int injectTime = vaccineInjectionResult.getInjectionSchedule().getInjectionTimes();
        int lastInjectTime = resultVaccine.getNumberOfInjection();
        if(injectTime == lastInjectTime){
            response.setNextInjectionDate(null);
        }else{
            response.setNextInjectionDate(vaccineInjectionResult.getInjectionDate().plusDays(resultVaccine.getTimeBeginNextInjection()));
        }

        //add customer
        CustomerResponse customerResponse = new CustomerResponse();
        customerMapper.employeeToCustomerResponse(customerResponse, vaccineInjectionResult.getCustomer());
        response.setCustomer(customerResponse);
        //add injection schedule
        InjectionScheduleResponse injectionScheduleResponse = new InjectionScheduleResponse();
        injectionScheduleMapper.injectionScheduleToInjectionScheduleResponse(injectionScheduleResponse, vaccineInjectionResult.getInjectionSchedule());
        response.setInjectionSchedule(injectionScheduleResponse);
        response.setInjectionTime(vaccineInjectionResult.getInjectionSchedule().getInjectionTimes());

        //add vaccine type
        response.getVaccine().setVaccineTypeId(resultVaccine.getVaccineType().getId());
        response.getVaccine().setVaccineTypeName(resultVaccine.getVaccineType().getVaccineTypeName());

        return response;
    }

    @Override
    @Transactional
    public void deleteVaccineInjectionResults(long[] ids) {
        for (long id : ids) {
            vaccineInjectionResultRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vaccine injection result", "id", String.valueOf(id)));
            vaccineInjectionResultRepository.deleteById(id);
            }
    }
    @Override
    public ResultResponse getInforFromScheduleId(long scheduleId) {
        ResultResponse response = new ResultResponse();
        response.setInjectionDate(LocalDate.now());

        List<RegisterInject> list = registerInjectRepository.findByScheIdAndDate(scheduleId, LocalDate.now(),false);
        List<String> listCus = new ArrayList<>();
        List<CustomerCreateResponse> listCus1 = new ArrayList<>();
        for(RegisterInject r : list)
        {
            listCus.add(r.getVaccineEmployee().getUsername());
        }
        for(String r : listCus)
        {
            Optional<VaccineEmployee> cus = employeeRepository.findByUsername(r);
            if (cus.isPresent()) {
                VaccineEmployee customer = cus.get();
                CustomerCreateResponse customerCreateResponse = new CustomerCreateResponse();
                customerCreateResponse.setUsername(customer.getUsername());
                customerCreateResponse.setAddress(customer.getAddress());
                customerCreateResponse.setEmail(customer.getEmail());
                customerCreateResponse.setGender(customer.getGender());
                customerCreateResponse.setPhone(customer.getPhone());
                customerCreateResponse.setDateOfBirth(customer.getDateOfBirth());
                customerCreateResponse.setEmployeeName(customer.getEmployeeName());
                customerCreateResponse.setIdentityCard(customer.getIdentityCard());
          listCus1.add(customerCreateResponse);
            }
        }
        response.setCustomer(listCus1);
            Optional<VaccineInjectionSchedule> vaccineInjectionSchedule = injectionScheduleRepository.findById(scheduleId);
            if (vaccineInjectionSchedule.isPresent()) {
                VaccineInjectionSchedule schedule = vaccineInjectionSchedule.get();
                response.setVaccineName(schedule.getVaccine().getVaccineName());
                response.setVaccineType(schedule.getVaccine().getVaccineType().getVaccineTypeName());
                response.setInjection(1);
                response.setInjectionNextDate(LocalDate.now().plusDays(schedule.getVaccine().getTimeBeginNextInjection()));
                response.setSchedule(schedule.getVaccine().getVaccineName() + " " +
                        schedule.getPlace() + " From " + schedule.getStartDate() + " To " + schedule.getEndDate());
            } else {
                throw new APIException(HttpStatus.NOT_FOUND, "Schedule not found!");
            }
            return response;
        }
//        else {
//            throw new APIException(HttpStatus.NOT_FOUND, "No customer injections for today");
//        }
//    }



}
