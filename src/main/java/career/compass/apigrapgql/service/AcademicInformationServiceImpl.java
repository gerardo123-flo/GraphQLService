package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.AcademicInformationRequest;
import career.compass.apigrapgql.dto.AcademicInformationResponse;
import career.compass.apigrapgql.model.AcademicInformation;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.AcademicInformationRepository;
import career.compass.apigrapgql.repository.UserRepository;
// import career.compass.apigrapgql.service.AcademicInformationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicInformationServiceImpl implements AcademicInformationService {

    private final AcademicInformationRepository academicRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AcademicInformationResponse> findAllByUserId(Integer userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<AcademicInformation> pageResult = academicRepository.findAllByUserId(userId, pageable);
        
        return pageResult.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AcademicInformationResponse findByIdAndUserId(Integer id, Integer userId) {
        AcademicInformation entity = academicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Academic record not found or access denied"));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public AcademicInformationResponse create(Integer userId, AcademicInformationRequest request) {
        // 1. Obtenemos la referencia del usuario (necesario para la relación)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. Mapeamos a Entidad
        AcademicInformation entity = new AcademicInformation();
        entity.setUser(user); // Asignamos el dueño
        updateEntityFromRequest(entity, request);

        // 3. Guardamos
        AcademicInformation saved = academicRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public AcademicInformationResponse update(Integer id, Integer userId, AcademicInformationRequest request) {
        // 1. Buscamos asegurando que sea del usuario
        AcademicInformation entity = academicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Academic record not found or access denied"));

        // 2. Actualizamos campos
        updateEntityFromRequest(entity, request);

        // 3. Guardamos y retornamos
        return mapToResponse(academicRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Integer id, Integer userId) {
        AcademicInformation entity = academicRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Academic record not found or access denied"));
        
        academicRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AcademicInformationResponse> searchByCriteriaAndUserId(Integer userId, String institutionName, String degree) {
        List<AcademicInformation> results = academicRepository.searchByCriteria(userId, institutionName, degree);
        
        return results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countAllByUserId(Integer userId) {
        return academicRepository.countByUserId(userId);
    }

    // ------------------------------------------------------
    // Mappers Helpers (Manuales para evitar librerías extra)
    // ------------------------------------------------------

    private AcademicInformationResponse mapToResponse(AcademicInformation entity) {
        return AcademicInformationResponse.builder()
                .id(entity.getId())
                .institution(entity.getInstitution())
                .career(entity.getCareer())
                .average(entity.getAverage())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .inProgress(entity.getInProgress())
                .build();
    }

    private void updateEntityFromRequest(AcademicInformation entity, AcademicInformationRequest request) {
        entity.setInstitution(request.getInstitution());
        entity.setCareer(request.getCareer());
        entity.setAverage(request.getAverage());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        
        // Lógica por si viene nulo el inProgress (aunque el DTO ya valida un poco)
        if (request.getInProgress() != null) {
            entity.setInProgress(request.getInProgress());
        } else {
            // Si no lo mandan y no tiene fecha fin, asumimos true
            entity.setInProgress(entity.getEndDate() == null);
        }
    }
}