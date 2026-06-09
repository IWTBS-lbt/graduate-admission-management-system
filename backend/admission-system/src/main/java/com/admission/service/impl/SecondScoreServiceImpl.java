package com.admission.service.impl;

import com.admission.entity.SecondScore;
import com.admission.mapper.SecondScoreMapper;
import com.admission.service.SecondScoreService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecondScoreServiceImpl extends ServiceImpl<SecondScoreMapper, SecondScore> implements SecondScoreService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateScore(SecondScore secondScore) {
        return this.saveOrUpdate(secondScore);
    }

    @Override
    public Page<SecondScore> searchByKeyword(String keyword, Integer page, Integer pageSize) {
        LambdaQueryWrapper<SecondScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SecondScore::getExamId, keyword);
        return this.page(new Page<>(page, pageSize), wrapper);
    }
}
