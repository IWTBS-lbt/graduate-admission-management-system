package com.admission.service.impl;

import com.admission.entity.Major;
import com.admission.entity.Student;
import com.admission.mapper.MajorMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // ⚠️ 必须有这个注解，否则 Spring 找不到它
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMajorById(String majorCode) {
        // 1. 检查该专业下是否有考生
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Student::getMajorCode, majorCode);
        Long count = studentMapper.selectCount(wrapper);

        if (count > 0) {
            throw new RuntimeException("该专业下还有考生，无法删除！");
        }

        // 2. 没有考生，执行删除
        return this.removeById(majorCode);
    }
}