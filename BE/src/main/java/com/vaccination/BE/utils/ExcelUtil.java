package com.vaccination.BE.utils;

import com.vaccination.BE.entity.VaccineRule;
import com.vaccination.BE.entity.VaccineVaccine;
import com.vaccination.BE.entity.VaccineVaccineType;
import com.vaccination.BE.excepiton.exceptions.APIException;
import com.vaccination.BE.repository.VaccineRepository;
import com.vaccination.BE.repository.VaccineTypeRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class ExcelUtil {

    private static VaccineTypeRepository staticVaccineTypeRepository;
    private static VaccineRepository staticVaccineRepository;

    @Autowired
    public void setRepositories(VaccineTypeRepository vaccineTypeRepository, VaccineRepository vaccineRepository) {
        staticVaccineTypeRepository = vaccineTypeRepository;
        staticVaccineRepository = vaccineRepository;
    }

    public static List<VaccineVaccine> excelToData(MultipartFile file) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(file.getBytes());
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();

        List<VaccineVaccine> vaccineVaccineList = new ArrayList<>();

        // Skip header row
        if (iterator.hasNext()) {
            iterator.next(); // Skip header row
        }
        Set<String> vaccineNamesSet = new HashSet<>();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            VaccineVaccine vaccineVaccine = new VaccineVaccine();
            int currentRowNum = currentRow.getRowNum() + 1; // Adding 1 to the row number

            // Validate Vaccine Name
            String vaccineName = getStringCellValue(currentRow.getCell(0));
            if (vaccineName == null || vaccineName.trim().isEmpty()  ) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 1: Vaccine name cannot be null or empty");
            }
            if (!vaccineNamesSet.add(vaccineName)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 1: Duplicate vaccine name found: " + vaccineName);
            }
            if (staticVaccineRepository.existsByVaccineName(vaccineName)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 1: The vaccine exists!");
            }
            vaccineVaccine.setVaccineName(vaccineName);

            // Validate Vaccine Type ID
            if (currentRow.getCell(1).getCellType() != CellType.NUMERIC) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 2: Vaccine type ID must be numeric");
            }
            Long vaccineTypeId = (long) currentRow.getCell(1).getNumericCellValue();
            if (vaccineTypeId == null) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 2: Vaccine type ID cannot be null");
            }
            if (!staticVaccineTypeRepository.existsById(vaccineTypeId)) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 2: Vaccine type ID not found");
            }
            
            VaccineVaccineType vaccineVaccineType =staticVaccineTypeRepository.findByVaccineTypeId(vaccineTypeId);
            if(!vaccineVaccineType.isStatus()){
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 2: Vaccine type is expired");
            }
            VaccineVaccineType vaccineType = new VaccineVaccineType();
            vaccineType.setId(vaccineTypeId);
            vaccineVaccine.setVaccineType(vaccineType);

            // Validate Usage
            String usage = getStringCellValue(currentRow.getCell(2));
            if (usage == null || usage.trim().isEmpty()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 3: Usage cannot be null or empty");
            }
            vaccineVaccine.setUsage(usage);

            // Validate Indication
            String indication = getStringCellValue(currentRow.getCell(3));
            if (indication == null || indication.trim().isEmpty()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 4: Indication cannot be null or empty");
            }
            vaccineVaccine.setIndication(indication);

            // Validate Contraindication
            String contraindication = getStringCellValue(currentRow.getCell(4));
            if (contraindication == null || contraindication.trim().isEmpty()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 5: Contraindication cannot be null or empty");
            }
            String[] addCon = contraindication.split(";");
            Set<VaccineRule> vaccineRules = new LinkedHashSet<>();
            for (String addcon : addCon) {
                VaccineRule vaccineRule = new VaccineRule();
                vaccineRule.setVaccine(vaccineVaccine);
                vaccineRule.setContraindication(addcon);
                vaccineRules.add(vaccineRule);
            }
            vaccineVaccine.setContraindications(vaccineRules);
            // Validate Number of Injections
            Integer numberOfInjections = getIntegerCellValue(currentRow.getCell(5));
            if (numberOfInjections == null || numberOfInjections <= 0) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 6: Number of injections must be a positive integer");
            }
            vaccineVaccine.setNumberOfInjection(numberOfInjections);


            // Time Begin Next Injection
            Integer timeBeginNextInject = getIntegerCellValue(currentRow.getCell(6));
            if (timeBeginNextInject == null || timeBeginNextInject <= 0) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 6: time Begin Next Inject must be a positive integer");
            }
            vaccineVaccine.setTimeBeginNextInjection(timeBeginNextInject);

            // Validate and convert Time Begin Next Injection
//            LocalDate beginDate = null;
//            String beginDateString = getStringCellValue(currentRow.getCell(6));
//            if (beginDateString != null) {
//                try {
//                    double excelDateValue = Double.parseDouble(beginDateString);
//                    beginDate = LocalDate.of(1900, 1, 1).plusDays((int) (excelDateValue - 2));
//                   // vaccineVaccine.setTimeBeginNextInjection(beginDate);
//                } catch (NumberFormatException | DateTimeParseException e) {
//                    throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 7: Error parsing begin date: " + beginDateString);
//                }
//            }

//            // Validate and convert Time End Next Injection
//            LocalDate endDate = null;
//            String endDateString = getStringCellValue(currentRow.getCell(7));
//            if (endDateString != null) {
//                try {
//                    double excelDateValue = Double.parseDouble(endDateString);
//                    endDate = LocalDate.of(1900, 1, 1).plusDays((int) (excelDateValue - 2));
//                  //  vaccineVaccine.setTimeEndNextInjection(endDate);
//                } catch (NumberFormatException | DateTimeParseException e) {
//                    throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 8: Error parsing end date: " + endDateString);
//                }
//            }

            // Check if begin date is before end date
//            if (beginDate != null && endDate != null && !beginDate.isBefore(endDate)) {
//                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 7 and 8: Time Begin Next Injection must be before Time End Next Injection");
//            }
            // Validate total of Injections
            Integer totalOfInject = getIntegerCellValue(currentRow.getCell(7));
            if (totalOfInject == null || totalOfInject <= 0) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 7: Total Of Injectmust be a positive integer");
            }
            vaccineVaccine.setTotalInject(totalOfInject);
            // Validate Origin
            String origin = getStringCellValue(currentRow.getCell(8));
            if (origin == null || origin.trim().isEmpty()) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 9: Origin cannot be null or empty");
            }
            vaccineVaccine.setOrigin(origin);

            // Validate Status
            Boolean status = getBooleanCellValue(currentRow.getCell(9));
            if (status == null) {
                throw new APIException(HttpStatus.BAD_REQUEST, "Row " + currentRowNum + ", Column 10: Status cannot be null");
            }
            vaccineVaccine.setStatus(status);

            vaccineVaccineList.add(vaccineVaccine);
        }

        workbook.close();
        return vaccineVaccineList;
    }

    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return null;
    }

//    private static Long getLongCellValue(Cell cell) {
//        if (cell == null) {
//            return null;
//        }
//        if (cell.getCellType() == CellType.NUMERIC) {
//            return (long) cell.getNumericCellValue();
//        } else if (cell.getCellType() == CellType.STRING) {
//            try {
//                return Long.parseLong(cell.getStringCellValue().trim());
//            } catch (NumberFormatException e) {
//                System.err.println("Error parsing numeric value: " + cell.getStringCellValue());
//            }
//        }
//        return null;
//    }

    private static Integer getIntegerCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing numeric value: " + cell.getStringCellValue());
            }
        }
        return null;
    }

    private static Boolean getBooleanCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        return null;
    }
}
