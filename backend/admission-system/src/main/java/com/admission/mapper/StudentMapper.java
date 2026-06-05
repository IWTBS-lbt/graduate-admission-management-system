package com.admission.mapper;

import com.admission.entity.Student;
import com.admission.vo.AdmissionStatsVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    // ============ 第5天新增：统计方法 ============

    /**
     * 统计录取考生的年龄分布
     */
    @Select("SELECT " +
            "'年龄分布' AS type, " +
            "CONCAT(age, '岁') AS `key`, " +   // ⚠️ 这里加上了反引号
            "COUNT(*) AS count " +
            "FROM student s " +
            "INNER JOIN admission a ON s.exam_id = a.exam_id " +
            "WHERE a.is_admitted = 1 " +
            "GROUP BY age")
    List<AdmissionStatsVO> getAgeDistribution();

    /**
     * 统计录取考生的来源分布
     */
    @Select("SELECT " +
            "'来源分布' AS type, " +
            "source AS `key`, " +               // ⚠️ 这里加上了反引号
            "COUNT(*) AS count " +
            "FROM student s " +
            "INNER JOIN admission a ON s.exam_id = a.exam_id " +
            "WHERE a.is_admitted = 1 " +
            "GROUP BY source")
    List<AdmissionStatsVO> getSourceDistribution();

    /**
     * 统计录取考生的专业分布
     */
    @Select("SELECT " +
            "'专业分布' AS type, " +
            "m.major_name AS `key`, " +         // ⚠️ 这里加上了反引号
            "COUNT(*) AS count " +
            "FROM student s " +
            "INNER JOIN admission a ON s.exam_id = a.exam_id " +
            "INNER JOIN major m ON s.major_code = m.major_code " +
            "WHERE a.is_admitted = 1 " +
            "GROUP BY m.major_name")
    List<AdmissionStatsVO> getMajorDistribution();
}