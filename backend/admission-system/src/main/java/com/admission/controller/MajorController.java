package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Major;
import com.admission.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*") // ⬅️ 加在这一行！
@RequestMapping("/major")
public class MajorController {

    @Autowired
    private MajorService majorService;

    // 获取所有专业
    @GetMapping("/list")
    public Result list() {
        return Result.success(majorService.list());
    }

    // 添加专业
    @PostMapping("/add")
    public Result add(@RequestBody Major major) {
        majorService.save(major);
        return Result.success(null);
    }

    // 删除专业
    @DeleteMapping("/delete/{majorCode}")
    public Result delete(@PathVariable String majorCode) {
        try {
            majorService.removeMajorById(majorCode);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}