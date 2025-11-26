package career.compass.apigrapgql.repository;

import career.compass.apigrapgql.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {
    @Query("SELECT t FROM Test t WHERE t.testType.name = :typeName AND t.active = true")
    Optional<Test> findByTestTypeNameAndActiveTrue(@Param("typeName") String typeName);
}