package com.vaccination.BE.service.injectionschedule;

import com.vaccination.BE.dto.request.injection_schedule_request.InjectionScheduleRequest;
import com.vaccination.BE.dto.response.injection_schedule_response.InjectionScheduleResponse;
import com.vaccination.BE.dto.response.injection_schedule_response.SearchScheduleResponse;
import org.springframework.stereotype.Service;


public interface InjectionScheduleService {
    SearchScheduleResponse getListSchedule (int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
    InjectionScheduleResponse createInjectionSchedule(InjectionScheduleRequest request);
    InjectionScheduleResponse updateInjectionSchedule(long id,InjectionScheduleRequest request);
    InjectionScheduleResponse getInjectionScheduleById(long id);

    SearchScheduleResponse getListOpenSchedule(int pageNo, int pageSize, String sortBy, String sortDir, String nameOrId);
}
