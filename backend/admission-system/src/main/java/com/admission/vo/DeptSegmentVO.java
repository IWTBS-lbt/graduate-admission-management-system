package com.admission.vo;

import lombok.Data;

/**
 * 院系维度分数段统计 VO
 */
@Data
public class DeptSegmentVO {
    private String department;  // 院系名称
    private String segment;     // 分数段（0-200, 200-300, 300-400, 400-500, 500+）
    private Integer count;      // 人数
}
