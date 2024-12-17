package com.vaccination.BE;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.vaccination.BE.service.cloudinary.CloudinaryServiceImpl;
import com.vaccination.BE.utils.FileUploadUtil;
import io.github.cdimascio.dotenv.Dotenv;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.util.Map;

@SpringBootApplication
@EnableScheduling
@EnableWebMvc
@OpenAPIDefinition(
        info = @Info(
                title = "Vaccination Management API",
                description = "Vaccination Management API Documentation"
        ),
        externalDocs = @ExternalDocumentation(
                description = "Vaccination Management",
                url = "http://git.fa.edu.vn/lamnt86/vaccinationmanagement.git"
        )
)
public class BeApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeApplication.class, args);
	}
}
