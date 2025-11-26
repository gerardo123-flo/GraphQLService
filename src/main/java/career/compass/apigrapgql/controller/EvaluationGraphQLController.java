package career.compass.apigrapgql.controller;

import career.compass.apigrapgql.dto.EvaluationDetailResponse;
import career.compass.apigrapgql.dto.TestResponse;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.UserRepository;
import career.compass.apigrapgql.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EvaluationGraphQLController {

    private final EvaluationService evaluationService;
    private final UserRepository userRepository;

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public TestResponse getPersonalityTest() {
        return evaluationService.getPersonalityTest();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public TestResponse getVocationalInterestsTest() {
        return evaluationService.getVocationalInterestsTest();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT', 'ADMIN')")
    public TestResponse getCognitiveSkillsTest() {
        return evaluationService.getCognitiveSkillsTest();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('UNIVERSITY_STUDENT', 'HIGH_SCHOOL_STUDENT')")
    public EvaluationDetailResponse getEvaluationDetail(@Argument Integer evaluationId) {
        User user = getCurrentUser();

        if (user == null) {
            throw new RuntimeException("Authentication required");
        }

        return evaluationService.getEvaluationDetail(user.getId(), evaluationId);
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