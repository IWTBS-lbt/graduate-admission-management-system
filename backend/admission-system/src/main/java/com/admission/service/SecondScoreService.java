package com.admission.service;

import com.admission.entity.SecondScore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SecondScoreService extends IService<SecondScore> {
    boolean saveOrUpdateScore(SecondScore secondScore);

    // 按考号模糊搜索（分页）
    Page<SecondScore> searchByKeyword(String keyword, Integer page, Integer pageSize);
}
