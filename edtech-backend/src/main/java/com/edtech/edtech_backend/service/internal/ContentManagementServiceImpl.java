package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.ContentResponseDto;
import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.*;
import com.edtech.edtech_backend.model.ContentAccessType;
import com.edtech.edtech_backend.repository.*;
import com.edtech.edtech_backend.service.ContentManagementService;
import com.edtech.edtech_backend.service.SubscriptionAccessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentManagementServiceImpl implements ContentManagementService {

    private final VideoContentRepository videoContentRepository;
    private final TestSeriesRepository testSeriesRepository;
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final SubscriptionAccessService subscriptionAccessService;

    public ContentManagementServiceImpl(VideoContentRepository videoContentRepository,
                                        TestSeriesRepository testSeriesRepository,
                                        QuizRepository quizRepository,
                                        CourseRepository courseRepository,
                                        CategoryRepository categoryRepository,
                                        SubscriptionAccessService subscriptionAccessService) {
        this.videoContentRepository = videoContentRepository;
        this.testSeriesRepository = testSeriesRepository;
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.subscriptionAccessService = subscriptionAccessService;
    }

    // Admin methods
    @Override
    public VideoContent createVideo(VideoUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        VideoContent video = new VideoContent();
        video.setTitle(dto.getTitle());
        video.setCourse(course);
        // store internal stored filename/path (not an external URL)
        video.setVideoUrl(dto.getStoredFileName());
        video.setAccessType(dto.getAccessType()); // Use accessType from DTO
        return videoContentRepository.save(video);
    }

    @Override
    public TestSeries createTestPaper(TestPaperUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        TestSeries testSeries = new TestSeries();
        testSeries.setTitle(dto.getTitle());
        testSeries.setAccessType(dto.getAccessType()); // Use accessType from DTO
        // In a real app, you'd set the paper URL here
        return testSeriesRepository.save(testSeries);
    }

    @Override
    public Quiz createQuiz(QuizUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (course.getCategory() != null && !course.getCategory().getId().equals(category.getId())) {
            throw new RuntimeException("Selected category does not match the course category");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCourse(course);
        quiz.setCategory(category);
        quiz.setAccessType(dto.getAccessType() != null ? dto.getAccessType() : ContentAccessType.FREE);
        return quizRepository.save(quiz);
    }

    // Student methods
    @Override
    public List<ContentResponseDto<VideoContent>> getVideosByCourse(Long courseId, User user) {
        List<VideoContent> allVideos = videoContentRepository.findByCourseId(courseId);
        boolean hasActiveSubscription = subscriptionAccessService.hasActiveSubscription(user);

        return allVideos.stream().map(video -> {
            boolean isLocked = video.getAccessType() == ContentAccessType.PREMIUM && !hasActiveSubscription;
            // For premium content that is locked, you might want to clear the URL
            if (isLocked) {
                video.setVideoUrl(null); // Clear URL if locked
            }
            return new ContentResponseDto<>(video, isLocked, video.getAccessType());
        }).collect(Collectors.toList());
    }

    @Override
    public List<ContentResponseDto<TestSeries>> getTestPapersByCourse(Long courseId, User user) {
        // TestSeries is independent from Course. Ignore courseId and return all active test series.
        List<TestSeries> allTestSeries = testSeriesRepository.findByActiveTrueOrderByIdDesc();
        boolean hasActiveSubscription = subscriptionAccessService.hasActiveSubscription(user);

        return allTestSeries.stream().map(testSeries -> {
            boolean isLocked = testSeries.getAccessType() == ContentAccessType.PREMIUM && !hasActiveSubscription;
            return new ContentResponseDto<>(testSeries, isLocked, testSeries.getAccessType());
        }).collect(Collectors.toList());
    }

    @Override
    public List<ContentResponseDto<Quiz>> getQuizzesByCourse(Long courseId, User user) {
        List<Quiz> allQuizzes = quizRepository.findByCourseId(courseId);
        boolean hasActiveSubscription = subscriptionAccessService.hasActiveSubscription(user);

        return allQuizzes.stream().map(quiz -> {
            boolean isLocked = quiz.getAccessType() == ContentAccessType.PREMIUM && !hasActiveSubscription;
            // You might clear sensitive quiz data if locked
            return new ContentResponseDto<>(quiz, isLocked, quiz.getAccessType());
        }).collect(Collectors.toList());
    }
}
