package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.QuizUploadDto;
import com.edtech.edtech_backend.dto.TestPaperUploadDto;
import com.edtech.edtech_backend.dto.VideoUploadDto;
import com.edtech.edtech_backend.entity.*;
import com.edtech.edtech_backend.model.*;
import com.edtech.edtech_backend.repository.*;
import com.edtech.edtech_backend.service.ContentManagementService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContentManagementServiceImpl implements ContentManagementService {

    private final VideoContentRepository videoContentRepository;
    private final TestSeriesRepository testSeriesRepository;
    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public ContentManagementServiceImpl(VideoContentRepository videoContentRepository,
                                        TestSeriesRepository testSeriesRepository,
                                        QuizRepository quizRepository,
                                        CourseRepository courseRepository,
                                        SubscriptionRepository subscriptionRepository,
                                        UserRepository userRepository) {
        this.videoContentRepository = videoContentRepository;
        this.testSeriesRepository = testSeriesRepository;
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    // Admin methods
    @Override
    public VideoContent createVideo(VideoUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        VideoContent video = new VideoContent();
        video.setTitle(dto.getTitle());
        video.setCourse(course);
        video.setVideoUrl(dto.getVideoUrl());
        video.setFreeSample(dto.isFreeSample());
        return videoContentRepository.save(video);
    }

    @Override
    public TestSeries createTestPaper(TestPaperUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        TestSeries testSeries = new TestSeries();
        testSeries.setTitle(dto.getTitle());
        testSeries.setCourse(course);
        testSeries.setFree(dto.isFree());
        // In a real app, you'd set the paper URL here
        return testSeriesRepository.save(testSeries);
    }

    @Override
    public Quiz createQuiz(QuizUploadDto dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Quiz quiz = new Quiz();
        quiz.setTitle(dto.getTitle());
        quiz.setCourse(course);
        quiz.setFree(dto.isFree());
        return quizRepository.save(quiz);
    }

    // Student methods
    @Override
    public List<VideoContent> getVideosByCourse(Long courseId, Long userId) {
        boolean isSubscribed = isUserSubscribed(userId, courseId);
        List<VideoContent> allVideos = videoContentRepository.findByCourseId(courseId);

        if (isSubscribed) {
            return allVideos;
        } else {
            return allVideos.stream()
                    .filter(VideoContent::isFreeSample)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<TestSeries> getTestPapersByCourse(Long courseId, Long userId) {
        boolean isSubscribed = isUserSubscribed(userId, courseId);
        List<TestSeries> allTestPapers = testSeriesRepository.findByCourseId(courseId);

        if (isSubscribed) {
            return allTestPapers;
        } else {
            return allTestPapers.stream()
                    .filter(TestSeries::isFree)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Quiz> getQuizzesByCourse(Long courseId, Long userId) {
        boolean isSubscribed = isUserSubscribed(userId, courseId);
        List<Quiz> allQuizzes = quizRepository.findByCourseId(courseId);

        if (isSubscribed) {
            return allQuizzes;
        } else {
            return allQuizzes.stream()
                    .filter(Quiz::isFree)
                    .collect(Collectors.toList());
        }
    }

    private boolean isUserSubscribed(Long userId, Long courseId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        return subscriptionRepository.findByUserAndCourse(user, course)
                .map(sub -> sub.isActive() && sub.getEndDate().isAfter(LocalDate.now()))
                .orElse(false);
    }
}