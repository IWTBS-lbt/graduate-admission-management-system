package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Admission;
import com.admission.service.AdmissionService;
import com.admission.utils.CsvUtils;
import com.admission.vo.AdmissionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admission")
public class AdmissionController {

    private final AdmissionService admissionService;

    @PostMapping("/generate")
    public Result generateAdmissionList(@RequestParam Integer totalScoreLine) {
        try {
            List<Admission> list = admissionService.generateAdmissionList(totalScoreLine);
            return Result.success(list);
        } catch (Exception e) {
            return Result.fail("生成录取名单失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(admissionService.page(new Page<>(page, pageSize)));
    }

    /**
     * 录取名单详情（含考生姓名、专业名称）
     */
    @GetMapping("/detail")
    public Result detail(@RequestParam(defaultValue = "1") Integer page,
                         @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(admissionService.getDetailList(page, pageSize));
    }

    /**
     * 导出录取名单 CSV
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<AdmissionVO> list = admissionService.getAllDetail();
        String[] headers = {"考号", "姓名", "报考专业", "初试总分", "复试总分", "综合总分", "录取系别", "录取状态"};
        List<String[]> rows = list.stream()
                .map(v -> new String[]{
                        v.getExamId(),
                        v.getName(),
                        v.getMajorName(),
                        String.valueOf(v.getFirstTotal()),
                        String.valueOf(v.getSecondTotal()),
                        String.valueOf(v.getTotalScore()),
                        v.getDepartment(),
                        v.getIsAdmitted() == 1 ? "已录取" : "未录取"
                })
                .collect(Collectors.toList());
        CsvUtils.writeCsv(response, "录取名单", headers, rows);
    }
}
