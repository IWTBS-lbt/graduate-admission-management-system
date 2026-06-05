package com.admission.service.impl;

import com.admission.entity.SecondScore;
import com.admission.mapper.SecondScoreMapper;
import com.admission.service.SecondScoreService;
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
}