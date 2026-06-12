package com.admission.service.impl;

import com.admission.entity.FirstScore;
import com.admission.entity.Student;
import com.admission.mapper.FirstScoreMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.FirstScoreService;
import com.admission.vo.FirstScoreVO;
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
public class FirstScoreServiceImpl extends ServiceImpl<FirstScoreMapper, FirstScore> implements FirstScoreService {

    private final StudentMapper studentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateScore(FirstScore firstScore) {
        // MyBatis-Plus 的 saveOrUpdate 会自动判断主键是否存在
        return this.saveOrUpdate(firstScore);
    }

    @Override
    public List<FirstScoreVO> getEligibleList(Integer politicsLine, Integer englishLine,
                                              Integer professionalBaseLine, Integer totalScoreLine) {
        return baseMapper.selectEligibleStudents(
                politicsLine, englishLine, professionalBaseLine, totalScoreLine
        );
    }

    @Override
    public Page<FirstScore> searchByKeyword(String keyword, Integer page, Integer pageSize) {
        // 先按姓名查考生，获取匹配的考号列表
        List<String> nameMatchedIds = studentMapper.selectList(
            new LambdaQueryWrapper<Student>().like(Student::getName, keyword)
        ).stream().map(Student::getExamId).collect(Collectors.toList());

        LambdaQueryWrapper<FirstScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FirstScore::getExamId, keyword);
        if (!nameMatchedIds.isEmpty()) {
            wrapper.or().in(FirstScore::getExamId, nameMatchedIds);
        }
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
