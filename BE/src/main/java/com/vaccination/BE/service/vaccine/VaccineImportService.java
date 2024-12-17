package com.vaccination.BE.service.vaccine;

import com.vaccination.BE.entity.VaccineVaccine;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VaccineImportService {
    public static boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<VaccineVaccine> getVaccineDataFromExcel(InputStream inputStream) {
        List<VaccineVaccine> vaccine = new ArrayList<>();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet("vaccines");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vaccine;
    }
}
