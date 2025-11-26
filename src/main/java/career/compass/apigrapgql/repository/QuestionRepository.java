package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query(value = "SELECT * FROM questions WHERE test_id = :testId AND active = true " +
            "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomActiveQuestionsByTestId(
            @Param("testId") Integer testId,
            @Param("limit") Integer limit);
}