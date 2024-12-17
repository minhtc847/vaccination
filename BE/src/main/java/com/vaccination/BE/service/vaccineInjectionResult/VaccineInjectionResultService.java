package com.vaccination.BE.service.vaccineInjectionResult;

import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultForCreateRequest;
import com.vaccination.BE.dto.request.vaccine_injection_result_request.VaccineInjectionResultRequest;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.InjectionResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.SearchVaccineInjectionResultResponse;
import com.vaccination.BE.dto.request.vaccine_request.VaccineRequest;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.ResultResponse;
import com.vaccination.BE.dto.response.vaccine_injection_result_response.VaccineInjectionResultResponse;

public interface VaccineInjectionResultService {
    String createVaccineInjectionResult(VaccineInjectionResultForCreateRequest request);

    VaccineInjectionResultResponse updateVaccineInjectionResult(long id, VaccineInjectionResultRequest request);


    SearchVaccineInjectionResultResponse getAllInjectionResult(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);

    long[] getAllId(String nameOrId);

    InjectionResultResponse getVaccineInjectionResultById(long id);

    void deleteVaccineInjectionResults(long[] ids);

    ResultResponse getInforFromScheduleId(long scheduleId);
}
