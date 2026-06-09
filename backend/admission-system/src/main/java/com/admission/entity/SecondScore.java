package com.admission.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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

    // MySQL GENERATED ALWAYS AS (...) STORED 计算列，禁止 INSERT/UPDATE
    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Integer total;
}