package com.vaccination.BE.service.cloudinary;

import com.vaccination.BE.dto.response.cloudinary_response.CloudinaryResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
    public CloudinaryResponse uploadEmployeeFile(MultipartFile file, String fileName);
    public CloudinaryResponse uploadImage(final MultipartFile file);
    public void deleteImage(String publicId);
    public void deleteFile(String publicId);
    public String uploadHtml(@NotNull @NotEmpty MultipartFile content, String title);
    CloudinaryResponse uploadHtmlFile(MultipartFile file, String fileName);
}
