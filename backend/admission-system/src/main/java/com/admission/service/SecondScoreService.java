package com.admission.service;

import com.admission.entity.SecondScore;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SecondScoreService extends IService<SecondScore> {
    boolean saveOrUpdateScore(SecondScore secondScore);
}