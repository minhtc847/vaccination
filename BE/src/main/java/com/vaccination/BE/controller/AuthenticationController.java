package com.vaccination.BE.controller;

import com.nimbusds.jose.JOSEException;
import com.vaccination.BE.dto.request.employee_request.IntrospecRequest;
import com.vaccination.BE.dto.response.employee_response.IntrospecResponse;
import com.vaccination.BE.service.AuthenticationService;
import com.vaccination.BE.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/introspect")
    IntrospecResponse loginUser(@RequestBody IntrospecRequest request) throws ParseException, JOSEException {
        return authenticationService.introspect(request);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ResponseEntity.ok("Logout successful");
    }
}
