package com.admission.service.impl;

import com.admission.entity.Admission;
import com.admission.entity.FirstScore;
import com.admission.entity.SecondScore;
import com.admission.mapper.AdmissionMapper;
import com.admission.mapper.FirstScoreMapper;
import com.admission.mapper.SecondScoreMapper;
import com.admission.service.AdmissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdmissionServiceImpl extends ServiceImpl<AdmissionMapper, Admission> implements AdmissionService {

    @Autowired
    private FirstScoreMapper firstScoreMapper;

    @Autowired
    private SecondScoreMapper secondScoreMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Admission> generateAdmissionList(Integer totalScoreLine) {
        // 1. 先清空旧名单
        this.remove(null);

        // 2. 获取所有考生成绩
        List<FirstScore> firstScores = firstScoreMapper.selectList(null);
        List<Admission> admissionList = new ArrayList<>();

        for (FirstScore firstScore : firstScores) {
            String examId = firstScore.getExamId();
            SecondScore secondScore = secondScoreMapper.selectById(examId);
            if (secondScore == null) continue;

            Integer total = firstScore.getTotal() + secondScore.getTotal();
            if (total >= totalScoreLine) {
                Admission admission = new Admission();
                admission.setExamId(examId);
                admission.setFirstTotal(firstScore.getTotal());
                admission.setSecondTotal(secondScore.getTotal());
                admission.setIsAdmitted(1);
                admission.setDepartment("待定");
                admissionList.add(admission);
            }
        }

        // 3. 保存新名单
        if (!admissionList.isEmpty()) {
            this.saveBatch(admissionList);
        }

        // 4. ⚠️ 直接返回最新生成的名单（关键！）
        return this.list();
    }
}