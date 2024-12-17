package com.vaccination.BE.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBlacklistService {
    @NonFinal
    @Value("${jwt.signerkey}")
    protected String SING_KEW;
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    //Put token to blacklist
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    //Filter Blacklist every 12 hour
    @Scheduled(fixedRate = 3600000*12) // 3600000 milliseconds = 1 hour
    public void clearBlacklistedTokens() throws ParseException, JOSEException {
        Iterator<String> iterator = blacklistedTokens.iterator();
        while (iterator.hasNext()) {
            String token = iterator.next();
            if (!validateToken(token)) {
                iterator.remove();
            }
        }
    }
    public boolean validateToken(String token)throws JOSEException, ParseException  {

        JWSVerifier verifier = new MACVerifier(SING_KEW.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        return signedJWT.verify(verifier)&& expiryTime.after(new Date());
    }

}

