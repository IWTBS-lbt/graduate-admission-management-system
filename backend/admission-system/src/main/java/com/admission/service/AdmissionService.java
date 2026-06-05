package com.admission.service;

import com.admission.entity.Admission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AdmissionService extends IService<Admission> {
    // ⚠️ 修改返回类型：返回最新的录取名单
    List<Admission> generateAdmissionList(Integer totalScoreLine);
}