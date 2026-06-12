package com.admission.vo;

import lombok.Data;

/**
 * 院系维度各科成绩统计 VO
 */
@Data
public class DeptSubjectVO {
    private String department;       // 院系名称
    private Integer totalCount;      // 考生总数
    // 政治
    private Double politicsAvg;      // 政治平均分
    private Integer politicsPass;    // 政治及格人数
    private Integer politicsFail;    // 政治不及格人数
    // 外语
    private Double englishAvg;       // 外语平均分
    private Integer englishPass;     // 外语及格人数
    private Integer englishFail;     // 外语不及格人数
    // 专业基础
    private Double professionalAvg;  // 专业基础平均分
    private Integer professionalPass;// 专业基础及格人数
    private Integer professionalFail;// 专业基础不及格人数
}
