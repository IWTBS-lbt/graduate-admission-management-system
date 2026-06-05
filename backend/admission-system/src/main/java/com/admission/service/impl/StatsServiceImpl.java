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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatsServiceImpl implements StatsService {

    @Autowired
    private FirstScoreMapper firstScoreMapper;

    @Autowired
    private StudentMapper studentMapper;

    // 新增注入专业Mapper
    @Autowired
    private MajorMapper majorMapper;

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
        Integer total = list.stream().mapToInt(ScoreSegmentVO::getCount).sum();
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
     * 新增：各专业招生计划与实际录取对比统计
     */
    // ========== 新增方法（已修复） ==========
    @Override
    public Map<String, Object> getPlanVsActualStats() {
        // 1. 获取所有专业的计划招生数
        List<Major> majors = majorMapper.selectList(null);
        // 2. 统计每个专业实际录取人数
        // ⚠️ 注意：这里要把 Map<String, Integer> 改为 Map<String, Long>
        Map<String, Long> actualMap = new HashMap<>();
        for (Major major : majors) {
            LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Student::getMajorCode, major.getMajorCode());
            // ⚠️ 这里关键：把 "exam_id" 改为 Student::getExamId
            wrapper.inSql(Student::getExamId, "SELECT exam_id FROM admission WHERE is_admitted = 1");
            // ⚠️ 这里：把 int 改为 Long
            Long actual = studentMapper.selectCount(wrapper);
            actualMap.put(major.getMajorCode(), actual);
        }

        // 3. 组装返回数据
        List<Map<String, Object>> result = new ArrayList<>();
        for (Major major : majors) {
            Map<String, Object> item = new HashMap<>();
            item.put("majorName", major.getMajorName());
            item.put("planInside", major.getPlanInside());
            item.put("planOutside", major.getPlanOutside());
            item.put("planTotal", major.getPlanInside() + major.getPlanOutside());
            item.put("actual", actualMap.getOrDefault(major.getMajorCode(), 0L));
            result.add(item);
        }
        return Map.of("list", result);
    }
}