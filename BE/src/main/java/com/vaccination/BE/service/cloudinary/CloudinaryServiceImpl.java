package com.vaccination.BE.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vaccination.BE.dto.response.cloudinary_response.CloudinaryResponse;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.utils.FileUploadUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Transactional
    @Override
    public CloudinaryResponse uploadImage(final MultipartFile file) {
        final String fileName = "Customer_" + System.currentTimeMillis();
        assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final CloudinaryResponse response = uploadEmployeeFile(file, fileName);
        return response;
    }

    @Override
    @Transactional
    public CloudinaryResponse uploadEmployeeFile(MultipartFile file, String fileName) {
        try {
            // Upload any file
            final Map result = cloudinary.uploader()
                    .upload(file.getBytes(), Map.of(
                            "public_id", "images/" + fileName,
                            "folder", "images"
                    ));
            final String url = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder()
                    .publicId(publicId).url(url)
                    .build();
        } catch (IOException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Failed to upload file");
        }
    }

    @Override
    public String uploadHtml(MultipartFile content, String title) {
        // Generate the new file name with the current time in milliseconds
        final String fileName = "news_" + System.currentTimeMillis() + ".html";
        // String to MultipartFile
//        final MultipartFile htmlFile = new MockMultipartFile(fileName, fileName, "text/html", content.getBytes());
        final CloudinaryResponse response = uploadHtmlFile(content, fileName);
        return response.getUrl();
    }

    @Override
    @Transactional
    public CloudinaryResponse uploadHtmlFile(MultipartFile file, String fileName) {
        try {
            // Upload the HTML file
            final Map result = cloudinary.uploader()
                    .upload(file.getBytes(), Map.of(
                            "resource_type", "raw",
                            "public_id", "raw/" + fileName,
                            "folder", "news"
                    ));
            final String url = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");
            System.out.println("url = " + url + "\n publicId = " + publicId);
            return CloudinaryResponse.builder()
                    .publicId(publicId).url(url)
                    .build();
        } catch (IOException e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Failed to upload file");
        }
    }

    @Override
    public void deleteFile(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        String publicId = extractPublicIdFromUrl(url) + ".html";
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "raw"));
        } catch (Exception e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Failed to delete file");
        }
    }


    @Override
    public void deleteImage(String url) {
        if (url == null || url.isEmpty()) {
            return;
        }
        String publicId = extractPublicIdFromUrl(url);
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new APIException(HttpStatus.BAD_REQUEST, "Failed to delete file");
        }
    }

    public boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if (size > FileUploadUtil.MAX_FILE_SIZE) {
            throw new RuntimeException("Max file size is 2MB");
        }
        final String fileName = file.getOriginalFilename();
        if (!isAllowedExtension(fileName, pattern)) {
            throw new RuntimeException("Only jpg, png, gif, bmp file are allowed");
        }
    }

    public String getFileName(final String name) {
        System.out.println("name = " + name);
        String result = name.replaceAll("\\s+", "");
        final DateFormat dateFormat = new SimpleDateFormat(FileUploadUtil.DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FileUploadUtil.FILE_NAME_FORMAT, result, date);
    }

    public String extractPublicIdFromUrl(String url) {
        // Split the URL by '/' and get the parts
        String[] parts = url.split("/");

        // Check if the parts length is sufficient to include the public ID and folder path
        if (parts.length < 8) { // Typically, the length should be at least 8 for Cloudinary URLs
            throw new IllegalArgumentException("Invalid Cloudinary URL format");
        }

        // Skip the version part and construct the public ID
        StringBuilder publicIdBuilder = new StringBuilder();
        // Start from the 8th element (index 7) to skip the version
        for (int i = 7; i < parts.length - 1; i++) {
            publicIdBuilder.append(parts[i]).append("/");
        }

        // Handle the last part separately to remove the file extension
        String publicIdWithExtension = parts[parts.length - 1];
        int lastDotIndex = publicIdWithExtension.lastIndexOf('.');
        if (lastDotIndex == -1) {
            throw new IllegalArgumentException("Invalid Cloudinary URL format");
        }
        String publicId = publicIdWithExtension.substring(0, lastDotIndex);
        publicIdBuilder.append(publicId);

        return publicIdBuilder.toString();
    }

    public String adjustPublicId(String originalPublicId) {
        // Split the public ID by '/'
        String[] parts = originalPublicId.split("/");

        // Ensure there are enough parts to handle
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid public ID format");
        }

        // Rebuild the public ID by rearranging folder and file name
        String fileName = parts[parts.length - 1]; // Get the last part (file name)
        StringBuilder newPublicIdBuilder = new StringBuilder();

        // Append parts in the desired order
        for (int i = 1; i < parts.length - 1; i++) {
            newPublicIdBuilder.append(parts[i]).append("/");
        }

        // Append file name
        newPublicIdBuilder.append(fileName);

        return newPublicIdBuilder.toString();
    }
}
