package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Student;
import com.admission.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        studentService.save(student);
        return Result.success(null);
    }

    @GetMapping("/list")
    public Result list() {
        return Result.success(studentService.list());
    }

    @DeleteMapping("/delete/{examId}")
    public Result delete(@PathVariable String examId) {
        studentService.removeById(examId);
        return Result.success(null);
    }

    // ✅ 修改考生信息（带详细错误提示）
    @PutMapping("/update")
    public Result update(@RequestBody Student student) {
        try {
            boolean success = studentService.updateById(student);
            if (success) {
                return Result.success(null);
            } else {
                return Result.fail("修改失败：未找到该考生信息");
            }
        } catch (DataIntegrityViolationException e) {
            // 捕获外键约束异常（最可能的原因：修改了专业代码或报考类别，但新值不存在）
            return Result.fail("修改失败：数据违反外键约束，请检查您修改的'专业代码'或'报考类别'是否正确，确保它们在对应的字典表中存在。");
        } catch (Exception e) {
            // 捕获其他所有异常
            e.printStackTrace();
            return Result.fail("修改失败：" + e.getMessage());
        }
    }
}