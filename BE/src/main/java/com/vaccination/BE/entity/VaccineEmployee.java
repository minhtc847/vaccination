    package com.vaccination.BE.entity;

    import com.vaccination.BE.dto.response.customer_report_response.CustomerReportResponse;
    import jakarta.persistence.*;
    import lombok.*;
    import lombok.experimental.FieldDefaults;
    import org.springframework.format.annotation.DateTimeFormat;

    import java.time.LocalDate;
    import java.util.Set;


    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Table(name = "vaccine_employee")
    public class VaccineEmployee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "EMPLOYEE_ID", nullable = false, length = 36)
         long id;

        @Column(name = "ADDRESS")
         String address;

        @Column(name = "DATE_OF_BIRTH")
        @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
        LocalDate dateOfBirth;

        @Column(name = "EMAIL", length = 100, unique = true)
         String email;

        @Column(name = "EMPLOYEE_NAME", length = 100)
         String employeeName;

        @Column(name = "GENDER", length = 10)
         String gender;

        @Column(name = "IMAGE")
         String image;

        @Column(name = "PASSWORD")
         String password;

        @Column(name = "PHONE", length = 20, unique = true)
         String phone;

        @Column(name = "POSITION", length = 100)
         String position;

        @Column(name = "USERNAME")
         String username;

        @Column(name = "WORKING_PLACE")
         String workingPlace;

        @Column(name ="IDENTITY_CARD")
        String identityCard;

        @JoinTable(name = "employee_role",
                joinColumns = @JoinColumn(name = "EMPLOYEE_ID"),
                inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
        @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        Set<VaccineRole> roles;

    //    @Version
        @Column(name = "version")
        Long version;

    }