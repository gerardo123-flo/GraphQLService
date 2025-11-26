package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.SkillRequest;
import career.compass.apigrapgql.dto.SkillResponse;
import career.compass.apigrapgql.dto.UpdateSkillRequest;

import java.util.List;

public interface SkillService {
    
    List<SkillResponse> findAllByUserId(Integer userId, int page, int pageSize);

    SkillResponse findByIdAndUserId(Integer id, Integer userId);

    SkillResponse create(Integer userId, SkillRequest request);

    SkillResponse update(Integer id, Integer userId, UpdateSkillRequest request);

    void delete(Integer id, Integer userId);
}