package com.vaccination.BE.dto.request.news_request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsRequest {
    @Size(max = 1000, message = "Preview must be less than 1000 character")
    private String preview;

    @NotNull(message = "Please input title")
    @NotEmpty(message = "Please input title")
    @Size(max = 300, message = "Title must be less than 300 character")
    private String title;

    @NotNull(message = "Please input content")
    private MultipartFile content;
}
