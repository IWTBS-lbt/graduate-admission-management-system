package com.admission.service.impl;

import com.admission.entity.Admission;
import com.admission.entity.FirstScore;
import com.admission.entity.Major;
import com.admission.entity.SecondScore;
import com.admission.entity.Student;
import com.admission.mapper.AdmissionMapper;
import com.admission.mapper.FirstScoreMapper;
import com.admission.mapper.MajorMapper;
import com.admission.mapper.SecondScoreMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.AdmissionService;
import com.admission.vo.AdmissionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdmissionServiceImpl extends ServiceImpl<AdmissionMapper, Admission> implements AdmissionService {

    private final FirstScoreMapper firstScoreMapper;
    private final SecondScoreMapper secondScoreMapper;
    private final StudentMapper studentMapper;
    private final MajorMapper majorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Admission> generateAdmissionList() {
        // 1. 先清空旧名单
        this.remove(null);

        // 2. 获取所有初试成绩
        List<FirstScore> firstScores = firstScoreMapper.selectList(null);

        // 3. 批量查询复试成绩，构建 Map（避免 N+1 查询）
        List<String> examIds = firstScores.stream()
                .map(FirstScore::getExamId)
                .collect(Collectors.toList());
        List<SecondScore> secondScores = secondScoreMapper.selectBatchIds(examIds);
        Map<String, SecondScore> secondScoreMap = secondScores.stream()
                .collect(Collectors.toMap(SecondScore::getExamId, s -> s));

        // 4. 批量查询考生信息，获取 major_code
        List<Student> students = studentMapper.selectBatchIds(examIds);
        Map<String, String> majorCodeMap = students.stream()
                .collect(Collectors.toMap(Student::getExamId, Student::getMajorCode));

        // 5. 批量查询专业信息，构建两个 Map：cutoff 和 majorName
        List<Major> majors = majorMapper.selectList(null);
        Map<String, Integer> cutoffMap = majors.stream()
                .filter(m -> m.getCutoffLine() != null)
                .collect(Collectors.toMap(Major::getMajorCode, Major::getCutoffLine));
        Map<String, String> majorNameMap = majors.stream()
                .collect(Collectors.toMap(Major::getMajorCode, Major::getMajorName));

        // 6. 生成录取名单（使用各专业独立分数线）
        List<Admission> admissionList = new ArrayList<>();
        for (FirstScore firstScore : firstScores) {
            String examId = firstScore.getExamId();
            SecondScore secondScore = secondScoreMap.get(examId);
            if (secondScore == null) continue;

            String majorCode = majorCodeMap.getOrDefault(examId, "");
            Integer cutoff = cutoffMap.get(majorCode);
            // 未设置分数线的专业不录取
            if (cutoff == null) continue;

            Integer total = firstScore.getTotal() + secondScore.getTotal();
            if (total >= cutoff) {
                Admission admission = new Admission();
                admission.setExamId(examId);
                admission.setFirstTotal(firstScore.getTotal());
                admission.setSecondTotal(secondScore.getTotal());
                admission.setIsAdmitted(1);
                admission.setDepartment(majorNameMap.getOrDefault(majorCode, "待定"));
                admissionList.add(admission);
            }
        }

        // 7. 保存新名单
        if (!admissionList.isEmpty()) {
            this.saveBatch(admissionList);
        }

        // 8. 返回最新生成的名单
        return this.list();
    }

    @Override
    public Page<AdmissionVO> getDetailList(int page, int pageSize) {
        Page<AdmissionVO> p = new Page<>(page, pageSize);
        List<AdmissionVO> list = baseMapper.selectDetailList(p);
        return p.setRecords(list);
    }

    @Override
    public List<AdmissionVO> getAllDetail() {
        return baseMapper.selectAllDetail();
    }
}