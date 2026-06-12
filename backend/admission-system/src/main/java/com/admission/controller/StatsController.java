package com.admission.controller;

import com.admission.common.Result;
import com.admission.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {

    private final StatsService statsService;

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

    @GetMapping("/plan-vs-actual")
    public Result getPlanVsActualStats() {
        return Result.success(statsService.getPlanVsActualStats());
    }

    @GetMapping("/dept-subject")
    public Result getDeptSubjectStats() {
        return Result.success(statsService.getDeptSubjectStats());
    }

    @GetMapping("/dept-segment")
    public Result getDeptSegmentStats() {
        return Result.success(statsService.getDeptSegmentStats());
    }
}
