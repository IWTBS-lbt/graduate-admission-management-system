package com.admission.service.impl;

import com.admission.entity.FirstScore;
import com.admission.mapper.FirstScoreMapper;
import com.admission.service.FirstScoreService;
import com.admission.vo.FirstScoreVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FirstScoreServiceImpl extends ServiceImpl<FirstScoreMapper, FirstScore> implements FirstScoreService {

    @Autowired
    private FirstScoreMapper firstScoreMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdateScore(FirstScore firstScore) {
        // MyBatis-Plus 的 saveOrUpdate 会自动判断主键是否存在
        return this.saveOrUpdate(firstScore);
    }

    @Override
    public List<FirstScoreVO> getEligibleList(Integer politicsLine, Integer englishLine,
                                              Integer professionalBaseLine, Integer totalScoreLine) {
        return firstScoreMapper.selectEligibleStudents(
                politicsLine, englishLine, professionalBaseLine, totalScoreLine
        );
    }
}