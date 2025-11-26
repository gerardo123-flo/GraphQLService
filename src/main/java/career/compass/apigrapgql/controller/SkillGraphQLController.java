package career.compass.apigrapgql.controller;

import career.compass.apigrapgql.dto.SkillRequest;
import career.compass.apigrapgql.dto.SkillResponse;
import career.compass.apigrapgql.dto.UpdateSkillRequest;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.UserRepository;
import career.compass.apigrapgql.service.SkillService;
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
public class SkillGraphQLController {

    private final SkillService skillService;
    private final UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public List<SkillResponse> getSkills(
            @Argument int page,
            @Argument int pageSize) {

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        if (page < 0 || pageSize < 0 || (page == 0 && pageSize == 0)) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        return skillService.findAllByUserId(user.getId(), page, pageSize);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public SkillResponse getSkillById(@Argument Integer id) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return skillService.findByIdAndUserId(id, user.getId());
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public SkillResponse createSkill(@Argument SkillRequest input) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return skillService.create(user.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public SkillResponse updateSkill(
            @Argument Integer id,
            @Argument UpdateSkillRequest input) {

        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        return skillService.update(id, user.getId(), input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public Boolean deleteSkill(@Argument Integer id) {
        User user = getCurrentUser();
        if (user == null) throw new RuntimeException("Authentication required");

        skillService.delete(id, user.getId());
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