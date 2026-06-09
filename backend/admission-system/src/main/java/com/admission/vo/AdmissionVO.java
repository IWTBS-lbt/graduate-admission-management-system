package com.admission.vo;

import lombok.Data;

@Data
public class AdmissionVO {
    private String examId;      // 考号
    private String name;        // 考生姓名
    private String majorName;   // 报考专业
    private Integer firstTotal; // 初试总分
    private Integer secondTotal;// 复试总分
    private Integer totalScore; // 综合总分
    private String department;  // 录取系别
    private Integer isAdmitted; // 0-未录取，1-已录取
}
