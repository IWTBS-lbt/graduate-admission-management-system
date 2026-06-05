package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("first_score")
public class FirstScore {
    @TableId
    private String examId;
    private Integer politics;
    private Integer english;
    private Integer professionalBase;

    // ✅ 只保留字段，删掉 @TableField(insert = "false", update = "false")
    private Integer total;
}