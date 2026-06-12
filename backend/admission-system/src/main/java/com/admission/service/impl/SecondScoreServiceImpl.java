package com.admission.service.impl;

import com.admission.entity.SecondScore;
import com.admission.entity.Student;
import com.admission.mapper.SecondScoreMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.SecondScoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecondScoreServiceImpl extends ServiceImpl<SecondScoreMapper, SecondScore> implements SecondScoreService {

    private final StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateScore(SecondScore secondScore) {
        return this.saveOrUpdate(secondScore);
    }

    @Override
    public Page<SecondScore> searchByKeyword(String keyword, Integer page, Integer pageSize) {
        // 先按姓名查考生，获取匹配的考号列表
        List<String> nameMatchedIds = studentMapper.selectList(
            new LambdaQueryWrapper<Student>().like(Student::getName, keyword)
        ).stream().map(Student::getExamId).collect(Collectors.toList());

        LambdaQueryWrapper<SecondScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SecondScore::getExamId, keyword);
        if (!nameMatchedIds.isEmpty()) {
            wrapper.or().in(SecondScore::getExamId, nameMatchedIds);
        }
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
