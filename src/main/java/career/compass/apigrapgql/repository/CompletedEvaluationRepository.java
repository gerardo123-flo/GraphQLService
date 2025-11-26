package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.CompletedEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedEvaluationRepository extends JpaRepository<CompletedEvaluation, Integer> {
}
