package com.admission.service;

import com.admission.entity.Department;
import com.admission.vo.DepartmentVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DepartmentService extends IService<Department> {
    // 获取院系列表及其下专业
    List<DepartmentVO> listWithMajors();
}
