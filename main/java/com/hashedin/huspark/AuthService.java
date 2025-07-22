package com.hashedin.huspark.service;

import com.hashedin.huspark.dto.CourseRequestDTO;
import com.hashedin.huspark.dto.CourseResponseDTO;
import com.hashedin.huspark.entity.Course;
import com.hashedin.huspark.entity.User;
import com.hashedin.huspark.repository.CourseRepository;
import com.hashedin.huspark.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = modelMapper.map(dto, Course.class);
        course.setUser(user);

        Course saved = courseRepository.save(course);
        return toDTO(saved);
    }

    public CourseResponseDTO updateCourse(Long id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        modelMapper.map(dto, course); // update fields from DTO
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
        CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
        dto.setUserId(course.getUser().getId());
        dto.setUserName(course.getUser().getUserName());
        return dto;
    }
}
