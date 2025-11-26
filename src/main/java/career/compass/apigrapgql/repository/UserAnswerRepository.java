package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.UserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends JpaRepository<UserAnswer, Integer> {
    List<UserAnswer> findByEvaluationIdOrderByQuestionId(Integer evaluationId);
}
