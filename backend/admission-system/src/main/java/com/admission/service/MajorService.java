package com.admission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.admission.entity.Major;

public interface MajorService extends IService<Major> {
    // 删除专业（带外键检查）
    boolean removeMajorById(String majorCode);
}