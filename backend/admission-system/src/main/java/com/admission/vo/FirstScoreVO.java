package com.admission.vo;

import lombok.Data;

@Data
public class FirstScoreVO {
    private String examId;      // 考号
    private String name;        // 姓名
    private String majorCode;   // 专业代码
    private Integer politics;   // 政治
    private Integer english;    // 外语
    private Integer professionalBase; // 专业基础
    private Integer total;      // 总分（来自数据库生成列）
}