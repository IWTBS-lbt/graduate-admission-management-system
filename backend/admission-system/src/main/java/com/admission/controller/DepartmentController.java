package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.Department;
import com.admission.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/department")
public class DepartmentController {

    private final DepartmentService departmentService;

    /** 院系列表 */
    @GetMapping("/list")
    public Result list() {
        List<Department> list = departmentService.list();
        return Result.success(list);
    }

    /** 院系列表（含下属专业） */
    @GetMapping("/with-majors")
    public Result listWithMajors() {
        return Result.success(departmentService.listWithMajors());
    }

    /** 添加院系 */
    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        departmentService.save(department);
        return Result.success(null);
    }

    /** 修改院系 */
    @PutMapping("/update")
    public Result update(@RequestBody Department department) {
        departmentService.updateById(department);
        return Result.success(null);
    }

    /** 删除院系 */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id) {
        departmentService.removeById(id);
        return Result.success(null);
    }
}
