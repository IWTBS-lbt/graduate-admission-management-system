package com.admission.service.impl;

import com.admission.entity.Department;
import com.admission.entity.Major;
import com.admission.mapper.DepartmentMapper;
import com.admission.mapper.MajorMapper;
import com.admission.service.DepartmentService;
import com.admission.vo.DepartmentVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    private final MajorMapper majorMapper;

    @Override
    public List<DepartmentVO> listWithMajors() {
        List<Department> departments = this.list();
        List<Major> allMajors = majorMapper.selectList(null);
        List<DepartmentVO> result = new ArrayList<>();

        for (Department dept : departments) {
            DepartmentVO vo = new DepartmentVO();
            vo.setId(dept.getId());
            vo.setName(dept.getName());
            List<Major> deptMajors = new ArrayList<>();
            for (Major m : allMajors) {
                if (dept.getName().equals(m.getDepartment())) {
                    deptMajors.add(m);
                }
            }
            vo.setMajors(deptMajors);
            vo.setMajorCount(deptMajors.size());
            result.add(vo);
        }
        return result;
    }
}
