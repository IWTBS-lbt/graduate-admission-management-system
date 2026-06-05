package com.admission.service;

import com.admission.vo.AdmissionStatsVO;
import com.admission.vo.ScoreSegmentVO;
import com.admission.vo.SubjectStatsVO;

import java.util.List;
import java.util.Map;

public interface StatsService {

    /**
     * 获取各科成绩统计
     */
    Map<String, SubjectStatsVO> getSubjectStats();

    /**
     * 获取分数段统计
     */
    List<ScoreSegmentVO> getScoreSegmentStats();

    /**
     * 获取录取生源分析
     */
    Map<String, List<AdmissionStatsVO>> getAdmissionStats();

    // ⚠️ 新增：声明这个方法
    Map<String, Object> getPlanVsActualStats();
}