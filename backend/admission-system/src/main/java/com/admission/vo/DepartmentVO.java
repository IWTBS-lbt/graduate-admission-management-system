package com.admission.vo;

import com.admission.entity.Major;
import lombok.Data;

import java.util.List;

@Data
public class DepartmentVO {
    private Integer id;
    private String name;
    private Integer majorCount;      // 专业数量
    private List<Major> majors;      // 该院系下的专业列表
}
