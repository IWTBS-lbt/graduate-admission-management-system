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
    public Page<Student> search(String keyword, String political, Integer isFresh, String education,
                                 String source, String majorCode, String type, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        // 考号或姓名模糊匹配
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Student::getExamId, keyword.trim())
                               .or()
                               .like(Student::getName, keyword.trim()));
        }
        // 精确筛选条件
        if (political != null && !political.trim().isEmpty()) {
            wrapper.eq(Student::getPolitical, political.trim());
        }
        if (isFresh != null) {
            wrapper.eq(Student::getIsFresh, isFresh);
        }
        if (education != null && !education.trim().isEmpty()) {
            wrapper.eq(Student::getEducation, education.trim());
        }
        if (source != null && !source.trim().isEmpty()) {
            wrapper.like(Student::getSource, source.trim());
        }
        if (majorCode != null && !majorCode.trim().isEmpty()) {
            wrapper.eq(Student::getMajorCode, majorCode.trim());
        }
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(Student::getType, type.trim());
        }
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
