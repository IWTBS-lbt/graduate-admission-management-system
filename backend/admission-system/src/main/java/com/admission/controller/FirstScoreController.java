package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.FirstScore;
import com.admission.service.FirstScoreService;
import com.admission.vo.FirstScoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/first_score")
public class FirstScoreController {

    @Autowired
    private FirstScoreService firstScoreService;

    /**
     * 录入/修改初试成绩
     */
    @PostMapping("/save")
    public Result saveScore(@RequestBody FirstScore firstScore) {
        boolean success = firstScoreService.saveOrUpdateScore(firstScore);
        return success ? Result.success(null) : Result.fail("成绩录入失败");
    }

    /**
     * 根据分数线筛选复试名单
     */
    @GetMapping("/check")
    public Result checkEligibility(@RequestParam Integer politicsLine,
                                   @RequestParam Integer englishLine,
                                   @RequestParam Integer professionalBaseLine,
                                   @RequestParam Integer totalScoreLine) {
        List<FirstScoreVO> list = firstScoreService.getEligibleList(
                politicsLine, englishLine, professionalBaseLine, totalScoreLine
        );
        return Result.success(list);
    }

    // ⚠️ 新增：获取所有初试成绩列表（用于前端显示）
    @GetMapping("/list")
    public Result list() {
        return Result.success(firstScoreService.list());
    }
}