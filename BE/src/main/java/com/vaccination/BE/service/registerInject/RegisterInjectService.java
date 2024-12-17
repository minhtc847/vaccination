package com.vaccination.BE.service.registerInject;

import com.vaccination.BE.dto.request.registerInject_request.RegisterInjectionRequest;
import com.vaccination.BE.dto.response.registerInject_response.InformationMailResponse;
import com.vaccination.BE.dto.response.registerInject_response.ListRegisterResponse;
import com.vaccination.BE.dto.response.registerInject_response.RegisterInjecctionResponse;
import com.vaccination.BE.entity.RegisterInject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface RegisterInjectService {
    ResponseEntity<RegisterInjecctionResponse> registerInject(RegisterInjectionRequest request);

    InformationMailResponse getInfor(String username, Long scheduleId);
    ListRegisterResponse getList(long cusId, LocalDate date, int page);
}
