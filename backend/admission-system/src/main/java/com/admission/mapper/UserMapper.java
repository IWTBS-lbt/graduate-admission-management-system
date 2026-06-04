package com.admission.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.admission.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}