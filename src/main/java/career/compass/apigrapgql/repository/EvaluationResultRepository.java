package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;



public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, Integer> {
}

