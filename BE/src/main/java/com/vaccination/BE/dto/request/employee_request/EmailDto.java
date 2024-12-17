package com.vaccination.BE.dto.request.employee_request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDto {
    private String to;
    private String subject;
    private String content;

    private Map<String, Object> props;

    public void putProp(String key, Object value) {
        if (props == null) {
            props = new HashMap<>();
        }
        props.put(key, value);
    }
}
