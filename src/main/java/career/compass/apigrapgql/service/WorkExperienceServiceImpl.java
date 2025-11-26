package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.WorkExperienceRequest;
import career.compass.apigrapgql.dto.WorkExperienceResponse;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.model.WorkExperience;
import career.compass.apigrapgql.repository.UserRepository;
import career.compass.apigrapgql.repository.WorkExperienceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkExperienceServiceImpl implements WorkExperienceService {

    private final WorkExperienceRepository workExperienceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<WorkExperienceResponse> findAllByUserId(Integer userId, int page, int pageSize) {
        Page<WorkExperience> pageResult = workExperienceRepository.findAllByUserId(userId, PageRequest.of(page, pageSize));
        return pageResult.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WorkExperienceResponse create(Integer userId, WorkExperienceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        WorkExperience entity = new WorkExperience();
        entity.setUser(user);
        updateEntityFromRequest(entity, request);

        return mapToResponse(workExperienceRepository.save(entity));
    }

    @Override
    @Transactional
    public WorkExperienceResponse update(Integer id, Integer userId, WorkExperienceRequest request) {
        // Buscamos internamente para asegurar que el usuario sea dueño antes de editar
        WorkExperience entity = workExperienceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Work experience not found or access denied"));

        updateEntityFromRequest(entity, request);

        return mapToResponse(workExperienceRepository.save(entity));
    }

    // --- Helpers ---

    private void updateEntityFromRequest(WorkExperience entity, WorkExperienceRequest request) {
        entity.setCompany(request.getCompany());
        entity.setPosition(request.getPosition());
        entity.setDescription(request.getDescription());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        
        if (request.getCurrentJob() != null) {
            entity.setCurrentJob(request.getCurrentJob());
        } else {
            entity.setCurrentJob(entity.getEndDate() == null);
        }
    }

    private WorkExperienceResponse mapToResponse(WorkExperience entity) {
        return WorkExperienceResponse.builder()
                .id(entity.getId())
                .company(entity.getCompany())
                .position(entity.getPosition())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .currentJob(entity.getCurrentJob())
                .build();
    }
}