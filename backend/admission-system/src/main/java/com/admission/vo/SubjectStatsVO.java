package com.admission.vo;

import lombok.Data;

@Data
public class SubjectStatsVO {
    private String subjectName;  // 科目名称：政治/英语/专业基础
    private Integer totalCount;  // 总人数
    private Double avgScore;     // 平均分
    private Integer passCount;   // 及格人数
    private Double passRate;     // 及格率
    private Integer failCount;   // 不及格人数
    private Double failRate;     // 不及格率
}