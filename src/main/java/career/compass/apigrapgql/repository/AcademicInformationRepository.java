package career.compass.apigrapgql.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import career.compass.apigrapgql.model.AcademicInformation;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicInformationRepository extends JpaRepository<AcademicInformation, Integer> {

    // 1. Buscar paginado pero SOLO del usuario
    Page<AcademicInformation> findAllByUserId(Integer userId, Pageable pageable);

    // 2. Buscar un registro específico validando que pertenezca al usuario
    Optional<AcademicInformation> findByIdAndUserId(Integer id, Integer userId);

    // 3. Contar registros del usuario
    long countByUserId(Integer userId);

    // 4. Búsqueda avanzada (Search) filtrada por usuario
    // La lógica (:param IS NULL OR ...) permite que los filtros sean opcionales
    @Query("SELECT a FROM AcademicInformation a WHERE a.user.id = :userId " +
           "AND (:institution IS NULL OR LOWER(a.institution) LIKE LOWER(CONCAT('%', :institution, '%'))) " +
           "AND (:career IS NULL OR LOWER(a.career) LIKE LOWER(CONCAT('%', :career, '%')))")
    List<AcademicInformation> searchByCriteria(
            @Param("userId") Integer userId,
            @Param("institution") String institution,
            @Param("career") String career);
}