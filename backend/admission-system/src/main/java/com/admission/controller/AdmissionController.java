package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Admission;
import com.admission.service.AdmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admission")
public class AdmissionController {

    @Autowired
    private AdmissionService admissionService;

    @PostMapping("/generate")
    public Result generateAdmissionList(@RequestParam Integer totalScoreLine) {
        try {
            // ⚠️ 直接接收 Service 返回的最新数据
            List<Admission> list = admissionService.generateAdmissionList(totalScoreLine);
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("生成录取名单失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(admissionService.list());
    }
}