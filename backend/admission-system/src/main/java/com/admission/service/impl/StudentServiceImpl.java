package com.admission.service.impl;

import com.admission.entity.Student;
import com.admission.mapper.StudentMapper;
import com.admission.service.StudentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Override
    public Page<Student> searchByKeyword(String keyword, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getExamId, keyword);
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
