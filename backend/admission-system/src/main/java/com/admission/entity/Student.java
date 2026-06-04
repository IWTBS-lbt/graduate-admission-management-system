package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student")
public class Student {

    // ⚠️ 一定要加上 @TableId
    @TableId
    private String examId;      // 对应数据库列：exam_id

    private String name;        // 对应：name
    private String gender;      // 对应：gender
    private Integer age;        // 对应：age
    private String political;   // 对应：political
    private Integer isFresh;    // 对应：is_fresh (0-否, 1-是)
    private String education;   // 对应：education
    private String source;      // 对应：source
    private String majorCode;   // 对应：major_code (⚠️这是外键，指向 major 表)
    private String type;        // 对应：type
}