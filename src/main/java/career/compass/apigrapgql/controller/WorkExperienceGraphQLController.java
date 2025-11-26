package career.compass.apigrapgql.controller;

import career.compass.apigrapgql.dto.WorkExperienceRequest;
import career.compass.apigrapgql.dto.WorkExperienceResponse;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.UserRepository;
import career.compass.apigrapgql.service.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WorkExperienceGraphQLController {

    private final WorkExperienceService workExperienceService;
    private final UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public List<WorkExperienceResponse> getWorkExperience(
            @Argument int page,
            @Argument int pageSize) {

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        return workExperienceService.findAllByUserId(user.getId(), page, pageSize);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public WorkExperienceResponse createWorkExperience(@Argument WorkExperienceRequest input) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        input.validateDateLogic();

        return workExperienceService.create(user.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public WorkExperienceResponse updateWorkExperience(
            @Argument Integer id,
            @Argument WorkExperienceRequest input) {

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        input.validateDateLogic();

        return workExperienceService.update(id, user.getId(), input);
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
