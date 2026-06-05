package com.admission.controller;

import com.admission.common.Result;
import com.admission.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/subject")
    public Result getSubjectStats() {
        return Result.success(statsService.getSubjectStats());
    }

    @GetMapping("/segment")
    public Result getScoreSegmentStats() {
        return Result.success(statsService.getScoreSegmentStats());
    }

    @GetMapping("/admission")
    public Result getAdmissionStats() {
        return Result.success(statsService.getAdmissionStats());
    }

    // ⚠️ 新增：计划 vs 实际接口
    @GetMapping("/plan-vs-actual")
    public Result getPlanVsActualStats() {
        return Result.success(statsService.getPlanVsActualStats());
    }
}