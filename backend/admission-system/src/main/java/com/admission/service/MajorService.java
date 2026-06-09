package com.admission.service;

import com.admission.entity.Major;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface MajorService extends IService<Major> {
    // 删除专业（带外键检查）
    boolean removeMajorById(String majorCode);

    // 按专业代码/名称模糊搜索（分页）
    Page<Major> searchByKeyword(String keyword, Integer page, Integer pageSize);

    // 修改专业信息
    boolean updateMajor(Major major);
}
