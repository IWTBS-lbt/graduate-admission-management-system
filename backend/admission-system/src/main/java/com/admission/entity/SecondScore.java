package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("second_score")
public class SecondScore {
    @TableId
    private String examId;
    private Integer professional;
    private Integer interview;
    private Integer computerTest;

    // ✅ 只保留字段，删掉 @TableField(insert = "false", update = "false")
    private Integer total;
}