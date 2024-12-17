package com.vaccination.BE.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import com.vaccination.BE.dto.request.employee_request.IntrospecRequest;
import com.vaccination.BE.dto.response.employee_response.IntrospecResponse;
import com.vaccination.BE.entity.VaccineEmployee;
import com.vaccination.BE.excepiton.exceptions.APIException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class AuthenticationService {
    @NonFinal
    @Value("${jwt.signerkey}")
    protected String SING_KEW;

    private TokenBlacklistService tokenBlacklistService;

    public AuthenticationService(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public String generateToken(VaccineEmployee employee){
//        header of token
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
//        payload of token
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(employee.getUsername())
                .issuer(employee.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(12, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("aud",employee.getPosition())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);

        //sign token
        try {
            jwsObject.sign(new MACSigner(SING_KEW));
            return jwsObject.serialize();
        } catch (JOSEException e) {
//            log.erroe("cannot create token",e);
            throw new RuntimeException(e);
        }
    }

//    private String buildScope(VaccineEmployee employee) {
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if (employee.getPosition() != null && !employee.getPosition().isEmpty()) {
//            stringJoiner.add(employee.getPosition());
//        }
//        return stringJoiner.toString();
//    }

    public IntrospecResponse introspect(IntrospecRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(SING_KEW.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospecResponse.builder()
                .valid(verified && expiryTime.after(new Date()) && !tokenBlacklistService.isTokenBlacklisted(token))
                .build();
    }

    public boolean validateToken(String token)throws JOSEException, ParseException  {

        JWSVerifier verifier = new MACVerifier(SING_KEW.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if(!expiryTime.after(new Date())){
            throw new APIException(HttpStatus.REQUEST_TIMEOUT,"Token expired");
        }
        return signedJWT.verify(verifier)&& expiryTime.after(new Date());
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //StringUtils.hasText(bearerToken) check null, empty or all space
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public String getUsernameFromToken(String token) {
        try {

            JWSVerifier verifier = new MACVerifier(SING_KEW.getBytes());

            SignedJWT signedJWT = SignedJWT.parse(token);

            if (signedJWT.verify(verifier)) {
                return signedJWT.getJWTClaimsSet().getSubject();
            } else {
                throw new RuntimeException("Token verification failed");
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error parsing or verifying token", e);
        }
    }
    public void logout(HttpServletRequest request) throws ParseException, JOSEException {
        SecurityContextHolder.clearContext();
        String jwt = getTokenFromRequest(request);
        if (jwt != null && validateToken(jwt)) {
            tokenBlacklistService.blacklistToken(jwt);
        }
    }
}
