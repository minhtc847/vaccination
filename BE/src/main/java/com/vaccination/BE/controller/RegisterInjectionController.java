package com.vaccination.BE.controller;

import com.vaccination.BE.dto.request.registerInject_request.RegisterInjectionRequest;
import com.vaccination.BE.dto.response.registerInject_response.InformationMailResponse;
import com.vaccination.BE.dto.response.registerInject_response.ListRegisterResponse;
import com.vaccination.BE.dto.response.registerInject_response.RegisterInjecctionResponse;
import com.vaccination.BE.entity.RegisterInject;
import com.vaccination.BE.service.registerInject.RegisterInjectImpl;
import com.vaccination.BE.utils.TokenEmailUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/injection/register")
@Slf4j
public class RegisterInjectionController {
    @Autowired
    RegisterInjectImpl registerInjectImpl;
    @Autowired
TokenEmailUtil tokenEmailUtil;
    @PostMapping()
    public ResponseEntity<RegisterInjecctionResponse> RegisterInjection(@RequestBody @Valid RegisterInjectionRequest request) {
        return registerInjectImpl.registerInject(request);
    }

    @GetMapping("/{token}")
    public ResponseEntity<InformationMailResponse> inforForRegister(@PathVariable String token) {
        Claims claims = tokenEmailUtil.decodeToken(token);
        String username = claims.get("username", String.class);
        Long scheduleId = claims.get("scheduleId", Long.class);

        return ResponseEntity.ok(registerInjectImpl.getInfor(username,scheduleId));
    }

    @GetMapping
    public ListRegisterResponse getListByCustomer(
            @RequestParam long cusId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam int page) {
        return registerInjectImpl.getList(cusId, date, page);
    }
}
