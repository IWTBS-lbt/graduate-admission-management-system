package com.admission.service.impl;

import com.admission.entity.Student;
import com.admission.mapper.StudentMapper;
import com.admission.service.StudentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service // ⚠️ 必须有这个注解，否则 Spring 找不到它
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
}