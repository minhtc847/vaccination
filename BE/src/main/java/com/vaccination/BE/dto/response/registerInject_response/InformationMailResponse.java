package com.vaccination.BE.dto.response.registerInject_response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vaccination.BE.entity.VaccineRule;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformationMailResponse {
    private String username;
    private String vaccineName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end;
    private String place;
    private int times;
    private String indication;
    private String[] contrainditation;
}
