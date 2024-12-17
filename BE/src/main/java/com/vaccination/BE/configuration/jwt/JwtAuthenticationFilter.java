package com.vaccination.BE.configuration.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.vaccination.BE.excepiton.ErrorDetails;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.excepiton.exceptions.NoTokenException;
import com.vaccination.BE.excepiton.exceptions.ResourceNotFoundException;
import com.vaccination.BE.service.AuthenticationService;
import com.vaccination.BE.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    AuthenticationService authenticationService;
    UserDetailsService userDetailsService;
    TokenBlacklistService tokenBlacklistService;

    public JwtAuthenticationFilter(AuthenticationService authenticationService, UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //skip JWT validation
        String token = getTokenFromRequest(request);

        try {
            if (StringUtils.hasText(token) && authenticationService.validateToken(token)) {
                //Validate from Blacklist
                if (tokenBlacklistService.isTokenBlacklisted(token)) {
                    throw new APIException(HttpStatus.FORBIDDEN, "Token is blacklisted");
                }
                // get username from token
                String username = authenticationService.getUsernameFromToken(token);
                //Load the user associated with token
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Authenticated user: " + username + " with role: " + userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (APIException e) {
            sendErrorResponse(response, e.getStatus().value(), e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //StringUtils.hasText(bearerToken) check null, empty or all space
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        if (!response.isCommitted()) {
            ErrorDetails error = new ErrorDetails(new Date(), message, null);
            ObjectMapper objectMapper = new ObjectMapper();
            String errorJson = objectMapper.writeValueAsString(error);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(statusCode);
            response.getWriter().write(errorJson);
            response.getWriter().close();
        }
    }
}
