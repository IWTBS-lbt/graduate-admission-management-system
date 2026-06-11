package com.admission.vo;

import lombok.Data;

@Data
public class InquiryVO {
    // 考生基本信息
    private String examId;
    private String name;
    private String majorName;
    private String department;

    // 初试成绩
    private Integer politics;
    private Integer english;
    private Integer professionalBase;
    private Integer firstTotal;

    // 复试成绩
    private Integer professional;
    private Integer interview;
    private Integer computerTest;
    private Integer secondTotal;

    // 综合总分
    private Integer combinedTotal;

    // 录取状态
    private Integer isAdmitted;
    private String admitDepartment;

    // 是否有成绩数据
    private boolean hasFirstScore;
    private boolean hasSecondScore;
    private boolean hasAdmission;
}
