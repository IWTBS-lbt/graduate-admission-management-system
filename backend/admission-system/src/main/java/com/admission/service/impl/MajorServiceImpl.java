package com.admission.service.impl;

import com.admission.entity.Major;
import com.admission.entity.Student;
import com.admission.mapper.MajorMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MajorServiceImpl extends ServiceImpl<MajorMapper, Major> implements MajorService {

    private final StudentMapper studentMapper;

    @Override
    public Page<Major> searchByKeyword(String keyword, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Major::getMajorCode, keyword)
                .or()
                .like(Major::getMajorName, keyword);
        return this.page(new Page<>(page, pageSize), wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMajor(Major major) {
        return this.updateById(major);
    }

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
