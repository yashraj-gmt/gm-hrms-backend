//package com.gm.hrms.config;
//
//import com.gm.hrms.entity.PersonalInformation;
//import com.gm.hrms.entity.PersonalInformationContact;
//import com.gm.hrms.entity.UserAuth;
//import com.gm.hrms.enums.*;
//import com.gm.hrms.repository.PersonalInformationRepository;
//import com.gm.hrms.repository.UserAuthRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//@RequiredArgsConstructor
//public class AdminInitializer implements CommandLineRunner {
//
//    private final UserAuthRepository authRepository;
//    private final PersonalInformationRepository personalRepo;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) {
//
//        if (authRepository.existsByUsername("admin@gm.com")) {
//            return;
//        }
//
//        // 1️⃣ Create Personal Information
//        PersonalInformation person = PersonalInformation.builder()
//                .firstName("System")
//                .middleName("Super")
//                .lastName("Admin")
//                .gender(Gender.MALE)
//                .dateOfBirth(LocalDate.of(1995,1,1))
//                .employmentType(EmploymentType.EMPLOYEE)
//                .maritalStatus(MaritalStatus.SINGLE)
//                .spouseOrParentName("NA")
//                .profileImageUrl("default.png")
//                .active(true)
//                .build();
//
//        PersonalInformationContact contact = PersonalInformationContact.builder()
//                .personalEmail("admin@gm.com")
//                .personalPhone("9999999999")
//                .emergencyPhone("9999999999")
//                .personalInformation(person)
//                .build();
//
//        person.setContact(contact);
//
//        personalRepo.save(person);
//
//        // 2️⃣ Create Auth
//        UserAuth auth = UserAuth.builder()
//                .personalInformation(person)
//                .username("admin@gm.com")
//                .passwordHash(passwordEncoder.encode("Admin@123"))
//                .role(RoleType.ADMIN)
//                .active(true)
//                .accountLocked(false)
//                .isLoggedIn(false)
//                .failedLoginAttempts(0)
//                .build();
//
//        authRepository.save(auth);
//
//        System.out.println("✅ Default Admin Created");
//    }
//}