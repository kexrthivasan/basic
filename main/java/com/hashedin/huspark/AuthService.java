package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.CourseRequestDTO;
import com.hashedin.huspark.dto.CourseResponseDTO;
import com.hashedin.huspark.entity.Course;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.repository.CourseRepository;
import com.hashedin.huspark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = Course.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .videoUrl(dto.getVideoUrl())
                .user(user)
                .build();

        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(dto.getTitle());
        course.setDescription(dto.getDescription());
        course.setVideoUrl(dto.getVideoUrl());

        Course updated = courseRepository.save(course);
        return toDTO(updated);
    }

    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return toDTO(course);
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    private CourseResponseDTO toDTO(Course course) {
        CourseResponseDTO dto = new CourseResponseDTO();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setVideoUrl(course.getVideoUrl());
        dto.setUserId(course.getUser().getId());
        dto.setUserName(course.getUser().getUserName());
        return dto;
    }
}
