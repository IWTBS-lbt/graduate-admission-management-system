package com.admission.service;

import com.admission.entity.FirstScore;
import com.admission.vo.FirstScoreVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface FirstScoreService extends IService<FirstScore> {

    /**
     * 保存或更新成绩
     */
    boolean saveOrUpdateScore(FirstScore firstScore);

    /**
     * 筛选复试名单
     */
    List<FirstScoreVO> getEligibleList(Integer politicsLine, Integer englishLine,
                                       Integer professionalBaseLine, Integer totalScoreLine);
}