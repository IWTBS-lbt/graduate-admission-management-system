package com.admission.service;

import com.admission.entity.Student;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface StudentService extends IService<Student> {
    // 按考号模糊搜索（分页）
    Page<Student> searchByKeyword(String keyword, Integer page, Integer pageSize);
}
