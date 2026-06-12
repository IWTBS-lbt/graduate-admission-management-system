package com.admission.service;

import com.admission.entity.Student;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface StudentService extends IService<Student> {
    // 多条件模糊搜索（分页）
    Page<Student> search(String keyword, String political, Integer isFresh, String education,
                         String source, String majorCode, String type, Integer page, Integer pageSize);
}
