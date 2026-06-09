package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Major;
import com.admission.service.MajorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/major")
public class MajorController {

    private final MajorService majorService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(majorService.page(new Page<>(page, pageSize)));
        }
        return Result.success(majorService.searchByKeyword(keyword.trim(), page, pageSize));
    }

    /**
     * 搜索自动补全：按专业代码/名称模糊匹配，返回前10条
     */
    @GetMapping("/suggest")
    public Result suggest(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        LambdaQueryWrapper<Major> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Major::getMajorCode, keyword.trim())
                .or()
                .like(Major::getMajorName, keyword.trim());
        List<Major> list = majorService.page(new Page<>(1, 10), wrapper).getRecords();
        List<Map<String, String>> result = list.stream()
                .map(m -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("value", m.getMajorCode());
                    item.put("label", m.getMajorCode() + " - " + m.getMajorName());
                    return item;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Major major) {
        majorService.save(major);
        return Result.success(null);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Major major) {
        majorService.updateMajor(major);
        return Result.success(null);
    }

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
