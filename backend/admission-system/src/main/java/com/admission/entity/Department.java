package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("department")
public class Department {
    @TableId
    private Integer id;
    private String name;
}
