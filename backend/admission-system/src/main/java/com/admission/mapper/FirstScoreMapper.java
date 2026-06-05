package com.admission.mapper;

import com.admission.entity.FirstScore;
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
            "SUM(CASE WHEN professional_base >= 60 THEN 1 ELSE 0 END) AS passCount, " +
            "SUM(CASE WHEN professional_base < 60 THEN 1 ELSE 0 END) AS failCount " +
            "FROM first_score")
    SubjectStatsVO getProfessionalBaseStats();

    @Select("SELECT " +
            "'0-200' AS segment, " +
            "COUNT(*) AS count " +
            "FROM first_score WHERE total < 200 " +
            "UNION ALL " +
            "SELECT '200-300', COUNT(*) FROM first_score WHERE total >= 200 AND total < 300 " +
            "UNION ALL " +
            "SELECT '300-400', COUNT(*) FROM first_score WHERE total >= 300 AND total < 400 " +
            "UNION ALL " +
            "SELECT '400-500', COUNT(*) FROM first_score WHERE total >= 400 AND total < 500 " +
            "UNION ALL " +
            "SELECT '500+', COUNT(*) FROM first_score WHERE total >= 500")
    List<ScoreSegmentVO> getScoreSegmentStats();
}