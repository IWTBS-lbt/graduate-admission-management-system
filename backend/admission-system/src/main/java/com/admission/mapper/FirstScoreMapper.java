package com.admission.mapper;

import com.admission.entity.FirstScore;
import com.admission.vo.DeptSegmentVO;
import com.admission.vo.DeptSubjectVO;
import com.admission.vo.FirstScoreVO;
import com.admission.vo.ScoreSegmentVO;
import com.admission.vo.SubjectStatsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FirstScoreMapper extends BaseMapper<FirstScore> {

    // ============ 原有方法（复试筛选） ============
    @Select("SELECT s.exam_id, s.name, s.major_code, f.politics, f.english, f.professional_base, f.total " +
            "FROM student s " +
            "INNER JOIN first_score f ON s.exam_id = f.exam_id " +
            "WHERE f.politics >= #{politicsLine} " +
            "  AND f.english >= #{englishLine} " +
            "  AND f.professional_base >= #{professionalBaseLine} " +
            "  AND f.total >= #{totalScoreLine}")
    List<FirstScoreVO> selectEligibleStudents(@Param("politicsLine") Integer politicsLine,
                                              @Param("englishLine") Integer englishLine,
                                              @Param("professionalBaseLine") Integer professionalBaseLine,
                                              @Param("totalScoreLine") Integer totalScoreLine);

    // ============ 新增方法（第5天：统计） ============
    @Select("SELECT " +
            "'政治' AS subjectName, " +
            "COUNT(*) AS totalCount, " +
            "AVG(politics) AS avgScore, " +
            "SUM(CASE WHEN politics >= 60 THEN 1 ELSE 0 END) AS passCount, " +
            "SUM(CASE WHEN politics < 60 THEN 1 ELSE 0 END) AS failCount " +
            "FROM first_score")
    SubjectStatsVO getPoliticsStats();

    @Select("SELECT " +
            "'外语' AS subjectName, " +
            "COUNT(*) AS totalCount, " +
            "AVG(english) AS avgScore, " +
            "SUM(CASE WHEN english >= 60 THEN 1 ELSE 0 END) AS passCount, " +
            "SUM(CASE WHEN english < 60 THEN 1 ELSE 0 END) AS failCount " +
            "FROM first_score")
    SubjectStatsVO getEnglishStats();

    @Select("SELECT " +
            "'专业基础' AS subjectName, " +
            "COUNT(*) AS totalCount, " +
            "AVG(professional_base) AS avgScore, " +
            "SUM(CASE WHEN professional_base >= 90 THEN 1 ELSE 0 END) AS passCount, " +
            "SUM(CASE WHEN professional_base < 90 THEN 1 ELSE 0 END) AS failCount " +
            "FROM first_score")
    SubjectStatsVO getProfessionalBaseStats();

    @Select("SELECT " +
            "'0-200' AS segment, " +
            "COUNT(*) AS count " +
            "FROM first_score WHERE total < 200 " +
            "UNION ALL " +
            "SELECT '200-250', COUNT(*) FROM first_score WHERE total >= 200 AND total < 250 " +
            "UNION ALL " +
            "SELECT '250-300', COUNT(*) FROM first_score WHERE total >= 250 AND total < 300 " +
            "UNION ALL " +
            "SELECT '300-350', COUNT(*) FROM first_score WHERE total >= 300 AND total <= 350")
    List<ScoreSegmentVO> getScoreSegmentStats();

    // ============ 院系维度统计 ============

    /**
     * 按院系统计各科平均分和及格情况
     */
    @Select("SELECT " +
            "COALESCE(m.department, '未知院系') AS department, " +
            "COUNT(*) AS totalCount, " +
            "ROUND(AVG(f.politics), 2) AS politicsAvg, " +
            "SUM(CASE WHEN f.politics >= 60 THEN 1 ELSE 0 END) AS politicsPass, " +
            "SUM(CASE WHEN f.politics < 60 THEN 1 ELSE 0 END) AS politicsFail, " +
            "ROUND(AVG(f.english), 2) AS englishAvg, " +
            "SUM(CASE WHEN f.english >= 60 THEN 1 ELSE 0 END) AS englishPass, " +
            "SUM(CASE WHEN f.english < 60 THEN 1 ELSE 0 END) AS englishFail, " +
            "ROUND(AVG(f.professional_base), 2) AS professionalAvg, " +
            "SUM(CASE WHEN f.professional_base >= 90 THEN 1 ELSE 0 END) AS professionalPass, " +
            "SUM(CASE WHEN f.professional_base < 90 THEN 1 ELSE 0 END) AS professionalFail " +
            "FROM first_score f " +
            "INNER JOIN student s ON f.exam_id = s.exam_id " +
            "LEFT JOIN major m ON s.major_code = m.major_code " +
            "GROUP BY m.department " +
            "ORDER BY m.department")
    List<DeptSubjectVO> getDeptSubjectStats();

    /**
     * 按院系统计各分数段人数
     */
    @Select("SELECT " +
            "COALESCE(m.department, '未知院系') AS department, " +
            "CASE " +
            "  WHEN f.total < 200 THEN '0-200' " +
            "  WHEN f.total < 250 THEN '200-250' " +
            "  WHEN f.total < 300 THEN '250-300' " +
            "  ELSE '300-350' " +
            "END AS segment, " +
            "COUNT(*) AS count " +
            "FROM first_score f " +
            "INNER JOIN student s ON f.exam_id = s.exam_id " +
            "LEFT JOIN major m ON s.major_code = m.major_code " +
            "GROUP BY m.department, segment " +
            "ORDER BY m.department, segment")
    List<DeptSegmentVO> getDeptSegmentStats();
}