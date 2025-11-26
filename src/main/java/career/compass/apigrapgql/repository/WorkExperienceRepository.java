package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.WorkExperience;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Integer> {
    Page<WorkExperience> findAllByUserId(Integer userId, Pageable pageable);
    Optional<WorkExperience> findByIdAndUserId(Integer id, Integer userId);
}