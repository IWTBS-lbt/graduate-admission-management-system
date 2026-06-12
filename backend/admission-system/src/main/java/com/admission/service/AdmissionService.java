package com.admission.service;

import com.admission.entity.Admission;
import com.admission.vo.AdmissionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AdmissionService extends IService<Admission> {
    // 生成录取名单（使用各专业独立分数线）
    List<Admission> generateAdmissionList();

    // 查询录取名单详情（含姓名、专业）
    Page<AdmissionVO> getDetailList(int page, int pageSize);

    // 获取全部录取详情（导出用）
    List<AdmissionVO> getAllDetail();
}