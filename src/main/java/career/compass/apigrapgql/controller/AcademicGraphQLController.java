package career.compass.apigrapgql.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import career.compass.apigrapgql.dto.AcademicInformationRequest;
import career.compass.apigrapgql.dto.AcademicInformationResponse;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.UserRepository;
import career.compass.apigrapgql.service.AcademicInformationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AcademicGraphQLController {

    private final AcademicInformationService academicInformationService;
    private final UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public List<AcademicInformationResponse> getAcademicInformation(
            @Argument int page,
            @Argument int pageSize) {

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        return academicInformationService.findAllByUserId(user.getId(), page, pageSize);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public AcademicInformationResponse getAcademicInformationById(@Argument Integer id) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return academicInformationService.findByIdAndUserId(id, user.getId());
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public List<AcademicInformationResponse> searchAcademicInformation(
            @Argument String institutionName,
            @Argument String degree) {
        
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return academicInformationService.searchByCriteriaAndUserId(user.getId(), institutionName, degree);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public Long getAcademicInformationCount() {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return academicInformationService.countAllByUserId(user.getId());
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public AcademicInformationResponse createAcademicInformation(@Argument AcademicInformationRequest input) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        input.validateDateLogic();

        return academicInformationService.create(user.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public AcademicInformationResponse updateAcademicInformation(
            @Argument Integer id,
            @Argument AcademicInformationRequest input) {
        
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        input.validateDateLogic();

        return academicInformationService.update(id, user.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public Boolean deleteAcademicInformation(@Argument Integer id) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        academicInformationService.delete(id, user.getId());
        return true; 
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }
}