package com.admission.service.impl;

import com.admission.entity.Major;
import com.admission.entity.Student;
import com.admission.mapper.FirstScoreMapper;
import com.admission.mapper.MajorMapper;
import com.admission.mapper.StudentMapper;
import com.admission.service.StatsService;
import com.admission.vo.AdmissionStatsVO;
import com.admission.vo.ScoreSegmentVO;
import com.admission.vo.SubjectStatsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final FirstScoreMapper firstScoreMapper;

    private final StudentMapper studentMapper;

    private final MajorMapper majorMapper;

    @Override
    public Map<String, SubjectStatsVO> getSubjectStats() {
        Map<String, SubjectStatsVO> result = new LinkedHashMap<>();

        SubjectStatsVO politics = firstScoreMapper.getPoliticsStats();
        SubjectStatsVO english = firstScoreMapper.getEnglishStats();
        SubjectStatsVO professional = firstScoreMapper.getProfessionalBaseStats();

        // 计算及格率和不及格率
        if (politics != null && politics.getTotalCount() > 0) {
            politics.setPassRate(politics.getPassCount() * 1.0 / politics.getTotalCount());
            politics.setFailRate(politics.getFailCount() * 1.0 / politics.getTotalCount());
        }
        if (english != null && english.getTotalCount() > 0) {
            english.setPassRate(english.getPassCount() * 1.0 / english.getTotalCount());
            english.setFailRate(english.getFailCount() * 1.0 / english.getTotalCount());
        }
        if (professional != null && professional.getTotalCount() > 0) {
            professional.setPassRate(professional.getPassCount() * 1.0 / professional.getTotalCount());
            professional.setFailRate(professional.getFailCount() * 1.0 / professional.getTotalCount());
        }

        result.put("politics", politics);
        result.put("english", english);
        result.put("professionalBase", professional);
        return result;
    }

    @Override
    public List<ScoreSegmentVO> getScoreSegmentStats() {
        List<ScoreSegmentVO> list = firstScoreMapper.getScoreSegmentStats();
        // 计算占比
        int total = list.stream().mapToInt(vo -> vo.getCount() != null ? vo.getCount() : 0).sum();
        if (total > 0) {
            for (ScoreSegmentVO vo : list) {
                vo.setPercentage(vo.getCount() * 1.0 / total);
            }
        }
        return list;
    }

    @Override
    public Map<String, List<AdmissionStatsVO>> getAdmissionStats() {
        Map<String, List<AdmissionStatsVO>> result = new LinkedHashMap<>();

        result.put("age", studentMapper.getAgeDistribution());
        result.put("source", studentMapper.getSourceDistribution());
        result.put("major", studentMapper.getMajorDistribution());

        return result;
    }

    /**
     * 各专业招生计划与实际录取对比统计
     */
    @Override
    public Map<String, Object> getPlanVsActualStats() {
        List<Major> majors = majorMapper.selectList(null);

        Map<String, Long> actualMap = new HashMap<>();
        for (Major major : majors) {
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getMajorCode, major.getMajorCode());
            wrapper.inSql(Student::getExamId, "SELECT exam_id FROM admission WHERE is_admitted = 1");
            Long actual = studentMapper.selectCount(wrapper);
            actualMap.put(major.getMajorCode(), actual);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Major major : majors) {
            Map<String, Object> item = new HashMap<>();
            item.put("majorName", major.getMajorName());
            item.put("planInside", major.getPlanInside());
            item.put("planOutside", major.getPlanOutside());
            int planTotal = (major.getPlanInside() != null ? major.getPlanInside() : 0)
                           + (major.getPlanOutside() != null ? major.getPlanOutside() : 0);
            item.put("planTotal", planTotal);
            item.put("actual", actualMap.getOrDefault(major.getMajorCode(), 0L));
            result.add(item);
        }
        return Map.of("list", result);
    }
}