package com.admission.vo;

import lombok.Data;

@Data
public class ScoreSegmentVO {
    private String segment;      // 分数段：0-200, 200-300, 300-400, 400-500, 500+
    private Integer count;       // 人数
    private Double percentage;   // 占比
}