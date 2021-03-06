package com.schManSys.sms.services;

import com.schManSys.sms.models.School;
import com.schManSys.sms.models.Student;
import com.schManSys.sms.models.Teacher;
import com.schManSys.sms.repository.SchoolRepository;
import com.schManSys.sms.repository.StudentRepository;
import com.schManSys.sms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SchoolServiceImpl implements SchoolService{

    private final SchoolRepository schoolRepository;
    private final StudentRepository studentRepository;

    @Override
    public School AddNewSchool(School school) {
        log.info("Adding new school to DB");
        return schoolRepository.save(school);
    }

    @Override
    public School FindSchoolById(Long schoolId) {
        return schoolRepository.findBySchoolId(schoolId);
    }

    @Override
    public School FindSchoolByName(String schoolName) {
        return schoolRepository.findBySchoolName(schoolName);
    }

    @Override
    public School EditSchoolById(Long schoolId, School school) {
        return null;
    }

    @Override
    public void EditSchoolByName(School schoolName, School school) {

    }

    @Override
    public School AddNewStudents(Long studentId,String schoolName) {

        Student student1 = studentRepository.findByStudentId(studentId);
        School school = schoolRepository.findBySchoolName(schoolName);

        school.getStudents().add(student1);

        return school;
    }

    @Override
    public School AddStuntByName(String student, String schoolName) {

        Student student1 = studentRepository.findByStudentName(student);
        School school = schoolRepository.findBySchoolName(schoolName);

        school.getStudents().add(student1);

        return school;
    }

    @Override
    public void AddNewTeacher(Teacher teacher) {

    }

    @Override
    public List<School> getSchools() {
        return schoolRepository.findAll();
    }
}
