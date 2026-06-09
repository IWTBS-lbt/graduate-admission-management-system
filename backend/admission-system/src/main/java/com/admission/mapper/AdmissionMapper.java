package com.admission.mapper;

import com.admission.entity.Admission;
import com.admission.vo.AdmissionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdmissionMapper extends BaseMapper<Admission> {

    /**
     * 查询录取名单详情（含考生姓名、专业名称）
     */
    @Select("SELECT a.exam_id AS examId, " +
            "s.name AS name, " +
            "COALESCE(m.major_name, a.department) AS majorName, " +
            "a.first_total AS firstTotal, " +
            "a.second_total AS secondTotal, " +
            "(a.first_total + a.second_total) AS totalScore, " +
            "a.department, " +
            "a.is_admitted AS isAdmitted " +
            "FROM admission a " +
            "LEFT JOIN student s ON a.exam_id = s.exam_id " +
            "LEFT JOIN major m ON s.major_code = m.major_code " +
            "ORDER BY totalScore DESC")
    List<AdmissionVO> selectDetailList(Page<AdmissionVO> page);

    /**
     * 统计录取人数
     */
    @Select("SELECT COUNT(*) FROM admission WHERE is_admitted = 1")
    Long countAdmitted();

    /**
     * 导出全部录取名单（不分页）
     */
    @Select("SELECT a.exam_id AS examId, " +
            "s.name AS name, " +
            "COALESCE(m.major_name, a.department) AS majorName, " +
            "a.first_total AS firstTotal, " +
            "a.second_total AS secondTotal, " +
            "(a.first_total + a.second_total) AS totalScore, " +
            "a.department, " +
            "a.is_admitted AS isAdmitted " +
            "FROM admission a " +
            "LEFT JOIN student s ON a.exam_id = s.exam_id " +
            "LEFT JOIN major m ON s.major_code = m.major_code " +
            "ORDER BY totalScore DESC")
    List<AdmissionVO> selectAllDetail();
}