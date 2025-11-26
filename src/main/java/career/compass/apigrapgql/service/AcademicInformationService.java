package career.compass.apigrapgql.service;

import java.util.List;

import career.compass.apigrapgql.dto.AcademicInformationRequest;
import career.compass.apigrapgql.dto.AcademicInformationResponse;

public interface AcademicInformationService {

    List<AcademicInformationResponse> findAllByUserId(Integer userId, int page, int pageSize);

    AcademicInformationResponse findByIdAndUserId(Integer id, Integer userId);

    AcademicInformationResponse create(Integer userId, AcademicInformationRequest request);

    AcademicInformationResponse update(Integer id, Integer userId, AcademicInformationRequest request);

    void delete(Integer id, Integer userId);

    List<AcademicInformationResponse> searchByCriteriaAndUserId(Integer userId, String institutionName, String degree);

    long countAllByUserId(Integer userId);
}