package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.WorkExperienceRequest;
import career.compass.apigrapgql.dto.WorkExperienceResponse;
import java.util.List;

public interface WorkExperienceService {

    List<WorkExperienceResponse> findAllByUserId(Integer userId, int page, int pageSize);

    WorkExperienceResponse create(Integer userId, WorkExperienceRequest request);

    WorkExperienceResponse update(Integer id, Integer userId, WorkExperienceRequest request);
}