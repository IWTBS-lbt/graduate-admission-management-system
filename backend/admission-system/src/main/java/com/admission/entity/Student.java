package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {

    @TableId
    private String examId;

    private String name;
    private String gender;
    private Integer age;
    private String political;
    private Integer isFresh;
    private String education;
    private String source;
    private String majorCode;
    private String type;
}