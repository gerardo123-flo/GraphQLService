package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.SkillRequest;
import career.compass.apigrapgql.dto.SkillResponse;
import career.compass.apigrapgql.dto.UpdateSkillRequest;
import career.compass.apigrapgql.model.Skill;
import career.compass.apigrapgql.model.User;
import career.compass.apigrapgql.repository.SkillRepository;
import career.compass.apigrapgql.repository.UserRepository;

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
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SkillResponse> findAllByUserId(Integer userId, int page, int pageSize) {
        Page<Skill> pageResult = skillRepository.findAllByUserId(userId, PageRequest.of(page, pageSize));
        return pageResult.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SkillResponse findByIdAndUserId(Integer id, Integer userId) {
        Skill skill = skillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found or access denied"));
        return mapToResponse(skill);
    }

    @Override
    @Transactional
    public SkillResponse create(Integer userId, SkillRequest request) {
        // 1. Obtener usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 2. Validar duplicidad (opcional, pero recomendado por tu constraint)
        if (skillRepository.existsByUserIdAndSkillName(userId, request.getSkillName())) {
            throw new IllegalArgumentException("You already have this skill registered.");
        }

        // 3. Crear Entidad
        Skill skill = new Skill();
        skill.setUser(user);
        skill.setSkillName(request.getSkillName());
        skill.setProficiencyLevel(request.getProficiencyLevel());

        // 4. Guardar
        return mapToResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public SkillResponse update(Integer id, Integer userId, UpdateSkillRequest request) {
        // 1. Buscar skill asegurando pertenencia
        Skill skill = skillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found or access denied"));

        // 2. Actualizar campos (Solo si vienen en el request, asumiendo que
        // UpdateSkillRequest puede tener nulos)
        if (request.getSkillName() != null) {
            skill.setSkillName(request.getSkillName());
        }
        if (request.getProficiencyLevel() != null) {
            skill.setProficiencyLevel(request.getProficiencyLevel());
        }

        return mapToResponse(skillRepository.save(skill));
    }

    @Override
    @Transactional
    public void delete(Integer id, Integer userId) {
        Skill skill = skillRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Skill not found or access denied"));
        skillRepository.delete(skill);
    }

    // Mapper manual simple
    private SkillResponse mapToResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .skillName(skill.getSkillName())
                .proficiencyLevel(skill.getProficiencyLevel())
                .build();
    }
}
