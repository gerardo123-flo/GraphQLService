package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    // Buscar paginado por usuario
    Page<Skill> findAllByUserId(Integer userId, Pageable pageable);

    // Buscar específico por usuario (Seguridad)
    Optional<Skill> findByIdAndUserId(Integer id, Integer userId);

    // Validar unicidad (Opcional, pero útil para evitar errores de BD)
    boolean existsByUserIdAndSkillName(Integer userId, String skillName);
}