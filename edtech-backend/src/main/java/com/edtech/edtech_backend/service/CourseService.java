package com.edtech.edtech_backend.service;

import com.edtech.edtech_backend.dto.CourseDto;
import com.edtech.edtech_backend.entity.Course;

import java.util.List;

public interface CourseService {
    Course createCourse(CourseDto courseDto);
    Course updateCourse(Long courseId, CourseDto courseDto);
    void deleteCourse(Long courseId);
    List<Course> getAllCourses();
    Course getCourseById(Long courseId);
}
