package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Student;
import com.admission.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // 添加考生
    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        studentService.save(student);
        return Result.success(null);
    }

    // 获取所有考生
    @GetMapping("/list")
    public Result list() {
        return Result.success(studentService.list());
    }

    // 删除考生
    @DeleteMapping("/delete/{examId}")
    public Result delete(@PathVariable String examId) {
        studentService.removeById(examId);
        return Result.success(null);
    }
}