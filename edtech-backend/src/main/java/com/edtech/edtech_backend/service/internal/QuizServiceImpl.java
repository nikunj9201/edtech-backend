package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.QuizResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.entity.Category;
import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.entity.Quiz;
import com.edtech.edtech_backend.entity.QuizQuestion;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.CategoryRepository;
import com.edtech.edtech_backend.repository.CourseRepository;
import com.edtech.edtech_backend.repository.QuizQuestionRepository;
import com.edtech.edtech_backend.repository.QuizRepository;
import com.edtech.edtech_backend.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    public QuizServiceImpl(QuizRepository quizRepository,
                          CourseRepository courseRepository,
                          CategoryRepository categoryRepository,
                          QuizQuestionRepository quizQuestionRepository) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.quizQuestionRepository = quizQuestionRepository;
    }

    @Override
    public Quiz createQuiz(QuizUploadDto quizUploadDto) {
        if (quizUploadDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz payload is required");
        }

        if (quizUploadDto.getTitle() == null || quizUploadDto.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quiz title cannot be blank");
        }

        if (quizUploadDto.getCourseId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required");
        }

        if (quizUploadDto.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }

        if (quizUploadDto.getAccessType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accessType is required");
        }

        Course course = courseRepository.findById(quizUploadDto.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        Category category = categoryRepository.findById(quizUploadDto.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (course.getCategory() != null && !course.getCategory().getId().equals(category.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected category does not match the course category");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizUploadDto.getTitle().trim());
        quiz.setDescription(quizUploadDto.getDescription());
        quiz.setCourse(course);
        quiz.setCategory(category);
        quiz.setAccessType(quizUploadDto.getAccessType() == null ? ContentAccessType.FREE : quizUploadDto.getAccessType());

        return quizRepository.save(quiz);
    }

    @Override
    public QuizResponseDto getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found"));

        List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
        return new QuizResponseDto(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getCourse() != null ? quiz.getCourse().getId() : null,
                quiz.getCategory() != null ? quiz.getCategory().getId() : null,
                quiz.getAccessType(),
                questions.size()
        );
    }

    @Override
    public List<QuizQuestion> getQuestionsByQuizId(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");
        }
        return quizQuestionRepository.findByQuizId(quizId);
    }
}
