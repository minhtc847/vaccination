package com.vaccination.BE.dto.request.vaccine_type_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusRequest {
    long id;
    boolean status;
}
