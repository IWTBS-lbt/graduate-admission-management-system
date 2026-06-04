package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("major")
public class Major {

    // ⚠️ 一定要加上 @TableId，告诉 MyBatis-Plus 这是主键
    @TableId
    private String majorCode;   // 对应数据库列：major_code

    private String majorName;   // 对应：major_name
    private Integer planInside; // 对应：plan_inside
    private Integer planOutside;// 对应：plan_outside
}