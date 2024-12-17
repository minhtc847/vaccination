//package com.vaccination.BE.configuration;
//
//
//import com.vaccination.BE.entity.VaccineEmployee;
//import com.vaccination.BE.entity.VaccineRole;
//import com.vaccination.BE.entity.VaccineVaccineType;
//import com.vaccination.BE.entity.VaccineEmployee;
//import com.vaccination.BE.entity.VaccineRole;
//import com.vaccination.BE.enums.Position;
//import com.vaccination.BE.repository.*;
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.Random;
//import java.util.Set;
//
//@Configuration
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Slf4j
//public class ApplicationInitConfig {
//    @Autowired
//    PasswordEncoder passwordEncoder;
//
//    @Bean
//    ApplicationRunner applicationRunner(EmployeeRepository employeeRepository, RoleRepository roleRepository,
//                                        VaccineTypeRepository vaccineTypeRepository, VaccineRepository vaccineRepository,
//                                        VaccineRuleRepository vaccineRuleRepository, VaccineInjectionResultRepository vaccineInjectionResultRepository,
//                                        InjectionScheduleRepository vaccineInjectionScheduleRepository) {
//        return args -> {
//            if (roleRepository.findByName(Position.ROLE_ADMIN.toString()).isEmpty()) {
//                VaccineRole admin = VaccineRole.builder().name(Position.ROLE_ADMIN.toString()).build();
//                roleRepository.save(admin);
//                VaccineRole employee = VaccineRole.builder().name(Position.ROLE_EMPLOYEE.toString()).build();
//                roleRepository.save(employee);
//                VaccineRole customer = VaccineRole.builder().name(Position.ROLE_CUSTOMER.toString()).build();
//                roleRepository.save(customer);
//            }
//
//            if (employeeRepository.findByUsername("admin").isEmpty()) {
//                VaccineEmployee employee = VaccineEmployee.builder()
//                        .username("admin123")
//                        .password(passwordEncoder.encode("admin123"))
//                        .email("admin@fsoft.com.vn")
//                        .position("ROLE_ADMIN")
//                        .phone("0987654321")
//                        .build();
//
//                employeeRepository.save(employee);
//                log.warn("admin user has been created with default password :admin");
//            }
//
//            if (employeeRepository.findByUsername("employee").isEmpty()) {
//                VaccineEmployee employee = VaccineEmployee.builder()
//                        .username("employee")
//                        .password(passwordEncoder.encode("employee"))
//                        .email("employee@fsoft.com.vn")
//                        .phone("0324254323")
//                        .build();
//                employeeRepository.save(employee);
//            }
//
//            // Create 100 random employee accounts
//            String[] names = {"John Doe", "Jane Smith", "Alice Johnson", "Robert Brown", "Lucy Miller",
//                    "Michael Davis", "Emma Wilson", "Daniel Garcia", "Sophia Anderson", "Matthew Taylor"};
//
//            Random random = new Random();
//            var roles = Position.ROLE_EMPLOYEE;
//            for (int i = 0; i < 100; i++) {
//                VaccineEmployee employee = new VaccineEmployee();
//                employee.setAddress("Address " + i);
//
//                // Generate LocalDate and convert to Date
//                LocalDate localDate = LocalDate.of(1990 + random.nextInt(30), random.nextInt(12) + 1, random.nextInt(28) + 1);
//
//                employee.setDateOfBirth(localDate);
//                employee.setEmail("employee" + i + "@example.com");
//                employee.setEmployeeName(names[random.nextInt(names.length)]);
//                employee.setGender(random.nextBoolean() ? "Male" : "Female");
//                employee.setPassword(passwordEncoder.encode("password" + i)); // Encode password
//                employee.setPhone("123-456-789" + i);
//                employee.setPosition(String.valueOf(roles));
//                employee.setUsername("username" + i);
//                employee.setWorkingPlace("Working Place " + i);
//
//                employeeRepository.save(employee);
//            }
//
//            // Create 10 random vaccine types
//            for (int i = 0; i < 10; i++) {
//                VaccineVaccineType vaccineType = new VaccineVaccineType();
//                vaccineType.setVaccineTypeName("Type " + i);
//                vaccineType.setStatus(true);
//                vaccineType.setDescription("Description type " + i);
//
//                vaccineTypeRepository.save(vaccineType);
//}
//
//            // Create 10 random vaccine rules
//            for (int i = 0; i < 10; i++) {
//                VaccineRule rule = new VaccineRule();
//                rule.setContraindication("Contraindication " + i);
//
//                vaccineRuleRepository.save(rule);
//            }
//
//            // Create 10 random vaccines
//            for (int i = 0; i < 10; i++) {
//                VaccineVaccine vaccine = new VaccineVaccine();
//                vaccine.setVaccineName("Vaccine " + i);
//                vaccine.setStatus(true);
//
//                vaccine.setIndication("Indication " + i);
//                vaccine.setNumberOfInjection(random.nextInt(4) + 1);
//                vaccine.setTimeBeginNextInjection(random.nextInt(30) + 1);
//                vaccine.setUsage("Usage " + i);
//                vaccine.setOrigin("Origin " + i);
//                vaccine.setTotalInject(random.nextInt(1000));
//                vaccine.setVaccineType(vaccineTypeRepository.findById((long) (i % 10 + 1)).orElse(null));
//
//                // Find the VaccineRule object or handle null case appropriately
//                VaccineRule vaccineRule = vaccineRuleRepository.findById((long) (i % 10 + 1)).orElse(null);
//
//                // Create a Set with the VaccineRule object, ensuring it's not null
//                Set<VaccineRule> rules = (vaccineRule != null) ? Set.of(vaccineRule) : Collections.emptySet();
//
//                // Assign the set of rules to the vaccine object
//                vaccine.setRules(rules);

//
//                vaccineRepository.save(vaccine);
//            }
//
//
//            // Create 10 random vaccine injection results
//            for (int i = 0; i < 10; i++) {
//                VaccineInjectionResult result = new VaccineInjectionResult();
//                result.setCustomer(employeeRepository.findById((long) (i % 100 + 1)).orElse(null));
//                result.setInjectionDate(LocalDate.now().minusDays(random.nextInt(365)));
//                result.setPlace("Place " + i);
//                result.setResult("Result " + i);
//                result.setVaccine(vaccineRepository.findById((long) (i % 10 + 1)).orElse(null));
//
//                vaccineInjectionResultRepository.save(result);
//            }
//
//            // Create 10 random vaccine injection schedules
//            for (int i = 0; i < 10; i++) {
//                VaccineInjectionSchedule schedule = new VaccineInjectionSchedule();
//                schedule.setDescription("Description " + i);
//                schedule.setEndDate(LocalDate.now().plusDays(random.nextInt(30)));
//                schedule.setPlace("Place " + i);
//                schedule.setStartDate(LocalDate.now().minusDays(random.nextInt(30)));
//                schedule.setVaccine(vaccineRepository.findById((long) (i % 10 + 1)).orElse(null));
//
//                vaccineInjectionScheduleRepository.save(schedule);
//            }
//        };
//    }
//}
