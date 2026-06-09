package com.admission.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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

    // MySQL GENERATED ALWAYS AS (...) STORED 计算列，禁止 INSERT/UPDATE
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer total;
}