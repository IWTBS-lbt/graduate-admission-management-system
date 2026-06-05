package com.admission.mapper;

import com.admission.entity.Admission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdmissionMapper extends BaseMapper<Admission> {
}