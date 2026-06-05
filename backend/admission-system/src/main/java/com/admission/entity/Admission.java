package com.admission.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admission")
public class Admission {
    @TableId
    private String examId;      // 考号（主键）
    private String department;  // 录取系别
    private Integer firstTotal; // 初试总分
    private Integer secondTotal;// 复试总分
    private Integer isAdmitted; // 0-未录取，1-已录取
}