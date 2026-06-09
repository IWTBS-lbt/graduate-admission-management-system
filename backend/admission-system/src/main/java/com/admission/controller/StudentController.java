package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Student;
import com.admission.service.StudentService;
import com.admission.utils.CsvUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/add")
    public Result add(@Valid @RequestBody Student student) {
        studentService.save(student);
        return Result.success(null);
    }

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(studentService.page(new Page<>(page, pageSize)));
        }
        return Result.success(studentService.searchByKeyword(keyword.trim(), page, pageSize));
    }

    @DeleteMapping("/delete/{examId}")
    public Result delete(@PathVariable String examId) {
        boolean removed = studentService.removeById(examId);
        if (removed) {
            return Result.success(null);
        } else {
            return Result.fail("删除失败：考生不存在");
        }
    }

    @PutMapping("/update")
    public Result update(@Valid @RequestBody Student student) {
        if (student.getExamId() == null || student.getExamId().isEmpty()) {
            return Result.fail("修改失败：缺少考生考号（examId）");
        }
        try {
            boolean success = studentService.updateById(student);
            if (success) {
                return Result.success(null);
            } else {
                return Result.fail("修改失败：未找到该考生信息");
            }
        } catch (DataIntegrityViolationException e) {
            return Result.fail("修改失败：数据违反外键约束，请检查您修改的'专业代码'或'报考类别'是否正确，确保它们在对应的字典表中存在。");
        } catch (Exception e) {
            log.error("修改考生信息失败", e);
            return Result.fail("修改失败：" + e.getMessage());
        }
    }

    /**
     * 搜索自动补全：按考号模糊匹配，返回前10条
     */
    @GetMapping("/suggest")
    public Result suggest(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Student::getExamId, keyword.trim());
        List<Student> list = studentService.page(new Page<>(1, 10), wrapper).getRecords();
        List<Map<String, String>> result = list.stream()
                .map(s -> {
                    Map<String, String> item = new HashMap<>();
                    item.put("value", s.getExamId());
                    item.put("label", s.getExamId() + " - " + s.getName());
                    return item;
                })
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 导出考生档案 CSV
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        List<Student> list = studentService.list();
        String[] headers = {"考号", "姓名", "性别", "年龄", "政治面貌", "是否应届", "学历", "来源", "专业代码", "报考类别"};
        List<String[]> rows = list.stream()
                .map(s -> new String[]{
                        s.getExamId(),
                        s.getName(),
                        s.getGender(),
                        String.valueOf(s.getAge()),
                        s.getPolitical(),
                        s.getIsFresh() == 1 ? "应届" : "往届",
                        s.getEducation(),
                        s.getSource(),
                        s.getMajorCode(),
                        s.getType()
                })
                .collect(Collectors.toList());
        CsvUtils.writeCsv(response, "考生档案", headers, rows);
    }
}
