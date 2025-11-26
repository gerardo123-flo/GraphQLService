package career.compass.apigrapgql.service;

import career.compass.apigrapgql.dto.*;
import career.compass.apigrapgql.model.*;
import career.compass.apigrapgql.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final CompletedEvaluationRepository completedEvaluationRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public TestResponse getPersonalityTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("personality")
                .orElseThrow(() -> new EntityNotFoundException("Personality test not found"));

        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        return buildTestResponse(test, randomQuestions);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getVocationalInterestsTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("vocational_interests")
                .orElseThrow(() -> new EntityNotFoundException("Vocational interests test not found"));

        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        return buildTestResponse(test, randomQuestions);
    }

    @Override
    @Transactional(readOnly = true)
    public TestResponse getCognitiveSkillsTest() {
        Test test = testRepository.findByTestTypeNameAndActiveTrue("cognitive_skills")
                .orElseThrow(() -> new EntityNotFoundException("Cognitive skills test not found"));

        List<Question> randomQuestions = questionRepository
                .findRandomActiveQuestionsByTestId(test.getId(), test.getQuestionsToShow());

        return buildTestResponse(test, randomQuestions);
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationDetailResponse getEvaluationDetail(Integer userId, Integer evaluationId) {
        // Verificar que la evaluación existe
        CompletedEvaluation evaluation = completedEvaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));

        // Si userId no es null, verificar que pertenezca al usuario
        if (userId != null && !evaluation.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This evaluation does not belong to the user");
        }

        // Obtener todas las respuestas del usuario para esta evaluación
        List<UserAnswer> userAnswers = userAnswerRepository
                .findByEvaluationIdOrderByQuestionId(evaluationId);

        // Mapear las respuestas al DTO
        List<UserAnswerDetail> answerDetails = userAnswers.stream()
                .map(this::mapUserAnswerToDetail)
                .collect(Collectors.toList());

        // Parsear el resultado del análisis
        Object analysisResult = parseAnalysisResult(evaluation);

        // Construir y retornar la respuesta
        return EvaluationDetailResponse.builder()
                .evaluationId(evaluation.getId())
                .testName(evaluation.getTest().getName())
                .testType(evaluation.getTest().getTestType().getName())
                .completionDate(evaluation.getCompletionDate())
                .totalScore(evaluation.getTotalScore() != null ?
                        evaluation.getTotalScore().toString() : "N/A")
                .answers(answerDetails)
                .analysisResult(analysisResult)
                .build();
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Construye un TestResponse a partir de un Test y sus preguntas
     */
    private TestResponse buildTestResponse(Test test, List<Question> questions) {
        // Mapear preguntas a DTOs
        List<QuestionResponse> questionResponses = questions.stream()
                .map(this::mapQuestionToResponse)
                .collect(Collectors.toList());

        // Construir y retornar TestResponse
        return TestResponse.builder()
                .id(test.getId())
                .name(test.getName())
                .description(test.getDescription())
                .testType(test.getTestType() != null ? test.getTestType().getName() : null)
                .questionsToShow(test.getQuestionsToShow())
                .questions(questionResponses)
                .build();
    }

    /**
     * Mapea una Question a QuestionResponse
     * IMPORTANTE: Carga las opciones desde el repository para evitar lazy loading
     */
    private QuestionResponse mapQuestionToResponse(Question question) {
        // Cargar opciones directamente desde el repository (evita lazy loading issues)
        List<AnswerOption> options = answerOptionRepository.findByQuestionId(question.getId());

        // Mapear opciones de respuesta
        List<AnswerOptionResponse> optionResponses = options.stream()
                .map(option -> AnswerOptionResponse.builder()
                        .id(option.getId())
                        .optionText(option.getOptionText())
                        .build())
                .collect(Collectors.toList());

        // Construir y retornar QuestionResponse
        return QuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .orderNumber(question.getOrderNumber())
                .options(optionResponses)
                .build();
    }

    /**
     * Mapea un UserAnswer a UserAnswerDetail
     */
    private UserAnswerDetail mapUserAnswerToDetail(UserAnswer userAnswer) {
        return UserAnswerDetail.builder()
                .questionId(userAnswer.getQuestion().getId())
                .questionText(userAnswer.getQuestion().getQuestionText())
                .selectedOptionId(userAnswer.getOption().getId())
                .selectedOptionText(userAnswer.getOption().getOptionText())
                .category(userAnswer.getOption().getCategory())
                .weightValue(userAnswer.getOption().getWeightValue())
                .build();
    }

    /**
     * Parsea el resultado del análisis de JSON a Object
     */
    private Object parseAnalysisResult(CompletedEvaluation evaluation) {
        if (evaluation.getEvaluationResult() != null &&
                evaluation.getEvaluationResult().getResultJson() != null) {
            try {
                return objectMapper.readValue(
                        evaluation.getEvaluationResult().getResultJson(),
                        Object.class);
            } catch (JsonProcessingException e) {
                // Si hay error al parsear, retornar el JSON como String
                return evaluation.getEvaluationResult().getResultJson();
            }
        }
        return null;
    }
}