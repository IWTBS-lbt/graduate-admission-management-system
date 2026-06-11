package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.*;
import com.admission.mapper.*;
import com.admission.vo.InquiryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final StudentMapper studentMapper;
    private final FirstScoreMapper firstScoreMapper;
    private final SecondScoreMapper secondScoreMapper;
    private final AdmissionMapper admissionMapper;
    private final MajorMapper majorMapper;

    /**
     * 考生查分：输入考号，返回全部成绩 + 录取状态
     */
    @GetMapping("/{examId}")
    public Result inquiry(@PathVariable String examId) {
        InquiryVO vo = new InquiryVO();
        vo.setExamId(examId);

        // 1. 考生基本信息
        Student student = studentMapper.selectById(examId);
        if (student == null) {
            vo.setName("考生信息未录入");
            return Result.success(vo);
        }
        vo.setName(student.getName());
        Major major = majorMapper.selectById(student.getMajorCode());
        if (major != null) {
            vo.setMajorName(major.getMajorName());
            vo.setDepartment(major.getDepartment());
        }

        // 2. 初试成绩
        FirstScore first = firstScoreMapper.selectById(examId);
        if (first != null) {
            vo.setPolitics(first.getPolitics());
            vo.setEnglish(first.getEnglish());
            vo.setProfessionalBase(first.getProfessionalBase());
            vo.setFirstTotal(first.getTotal());
            vo.setHasFirstScore(true);
        }

        // 3. 复试成绩
        SecondScore second = secondScoreMapper.selectById(examId);
        if (second != null) {
            vo.setProfessional(second.getProfessional());
            vo.setInterview(second.getInterview());
            vo.setComputerTest(second.getComputerTest());
            vo.setSecondTotal(second.getTotal());
            vo.setHasSecondScore(true);
        }

        // 4. 综合总分
        if (first != null && second != null) {
            vo.setCombinedTotal(first.getTotal() + second.getTotal());
        } else if (first != null) {
            vo.setCombinedTotal(first.getTotal());
        }

        // 5. 录取状态
        Admission admission = admissionMapper.selectById(examId);
        if (admission != null) {
            vo.setIsAdmitted(admission.getIsAdmitted());
            vo.setAdmitDepartment(admission.getDepartment());
            vo.setHasAdmission(true);
        }

        return Result.success(vo);
    }
}
