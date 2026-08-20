package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.dto.CourseDto;
import com.edtech.edtech_backend.entity.Category;
import com.edtech.edtech_backend.entity.Course;
import com.edtech.edtech_backend.repository.CategoryRepository;
import com.edtech.edtech_backend.repository.CourseRepository;
import com.edtech.edtech_backend.service.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    public CourseServiceImpl(CourseRepository courseRepository, CategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Course createCourse(CourseDto courseDto) {
        Category category = categoryRepository.findById(courseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Course course = new Course();
        course.setCategory(category);
        course.setTitle(courseDto.getTitle());
        course.setSubtitle(courseDto.getSubtitle());
        course.setThumbnailUrl(courseDto.getThumbnailUrl());
        course.setOriginalPrice(courseDto.getOriginalPrice());
        course.setDiscountedPrice(courseDto.getDiscountedPrice());
        
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long courseId, CourseDto courseDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Category category = categoryRepository.findById(courseDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        course.setCategory(category);
        course.setTitle(courseDto.getTitle());
        course.setSubtitle(courseDto.getSubtitle());
        course.setThumbnailUrl(courseDto.getThumbnailUrl());
        course.setOriginalPrice(courseDto.getOriginalPrice());
        course.setDiscountedPrice(courseDto.getDiscountedPrice());

        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }
}
