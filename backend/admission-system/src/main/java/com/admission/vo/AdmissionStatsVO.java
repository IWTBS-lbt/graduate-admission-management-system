package com.admission.vo;

import lombok.Data;

@Data
public class AdmissionStatsVO {
    private String type;         // 统计类型：年龄分布/来源分布/专业分布
    private String key;          // 具体值：23岁/大连民族大学/计算机科学与技术
    private Integer count;       // 人数
}