package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Integer> {
    List<AnswerOption> findByQuestionId(Integer questionId);
}