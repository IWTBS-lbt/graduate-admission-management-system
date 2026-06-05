package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.SecondScore;
import com.admission.service.SecondScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/second_score")
public class SecondScoreController {

    @Autowired
    private SecondScoreService secondScoreService;

    @PostMapping("/save")
    public Result saveScore(@RequestBody SecondScore secondScore) {
        boolean success = secondScoreService.saveOrUpdateScore(secondScore);
        return success ? Result.success(null) : Result.fail("复试成绩录入失败");
    }

    // ⚠️ 新增：获取所有复试成绩列表（可选，方便前端展示）
    @GetMapping("/list")
    public Result list() {
        return Result.success(secondScoreService.list());
    }
}