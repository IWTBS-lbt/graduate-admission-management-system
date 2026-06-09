package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.FirstScore;
import com.admission.service.FirstScoreService;
import com.admission.utils.CsvUtils;
import com.admission.vo.FirstScoreVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/first_score")
public class FirstScoreController {

    private final FirstScoreService firstScoreService;

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

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(firstScoreService.page(new Page<>(page, pageSize)));
        }
        return Result.success(firstScoreService.searchByKeyword(keyword.trim(), page, pageSize));
    }

    /**
     * 搜索自动补全：按考号模糊匹配已有初试成绩的考生
     */
    @GetMapping("/suggest")
    public Result suggest(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        LambdaQueryWrapper<FirstScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FirstScore::getExamId, keyword.trim());
        List<FirstScore> list = firstScoreService.page(new Page<>(1, 10), wrapper).getRecords();
        List<Map<String, String>> result = list.stream()
                .map(s -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("value", s.getExamId());
                    item.put("label", s.getExamId());
                    return item;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 导出初试成绩 CSV
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<FirstScore> list = firstScoreService.list();
        String[] headers = {"考号", "政治", "英语", "专业基础", "总分"};
        List<String[]> rows = list.stream()
                .map(s -> new String[]{
                        s.getExamId(),
                        String.valueOf(s.getPolitics()),
                        String.valueOf(s.getEnglish()),
                        String.valueOf(s.getProfessionalBase()),
                        String.valueOf(s.getTotal())
                })
                .collect(Collectors.toList());
        CsvUtils.writeCsv(response, "初试成绩", headers, rows);
    }
}
