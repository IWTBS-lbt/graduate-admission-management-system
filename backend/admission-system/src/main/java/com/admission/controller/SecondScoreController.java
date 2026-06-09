package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.SecondScore;
import com.admission.service.SecondScoreService;
import com.admission.utils.CsvUtils;
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
@RequestMapping("/second_score")
public class SecondScoreController {

    private final SecondScoreService secondScoreService;

    @PostMapping("/save")
    public Result saveScore(@RequestBody SecondScore secondScore) {
        boolean success = secondScoreService.saveOrUpdateScore(secondScore);
        return success ? Result.success(null) : Result.fail("复试成绩录入失败");
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(secondScoreService.page(new Page<>(page, pageSize)));
        }
        return Result.success(secondScoreService.searchByKeyword(keyword.trim(), page, pageSize));
    }

    /**
     * 搜索自动补全：按考号模糊匹配已有复试成绩的考生
     */
    @GetMapping("/suggest")
    public Result suggest(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        LambdaQueryWrapper<SecondScore> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SecondScore::getExamId, keyword.trim());
        List<SecondScore> list = secondScoreService.page(new Page<>(1, 10), wrapper).getRecords();
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
     * 导出复试成绩 CSV
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<SecondScore> list = secondScoreService.list();
        String[] headers = {"考号", "专业成绩", "面试成绩", "上机成绩", "复试总分"};
        List<String[]> rows = list.stream()
                .map(s -> new String[]{
                        s.getExamId(),
                        String.valueOf(s.getProfessional()),
                        String.valueOf(s.getInterview()),
                        String.valueOf(s.getComputerTest()),
                        String.valueOf(s.getTotal())
                })
                .collect(Collectors.toList());
        CsvUtils.writeCsv(response, "复试成绩", headers, rows);
    }
}
