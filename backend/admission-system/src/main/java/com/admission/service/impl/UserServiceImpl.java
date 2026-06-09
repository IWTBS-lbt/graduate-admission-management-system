package com.admission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.admission.entity.User;
import com.admission.mapper.UserMapper;
import com.admission.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User login(String username, String password) {
        // 先按用户名查找
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            return null;
        }

        String storedPassword = user.getPassword();

        // 1. 如果密码已是 BCrypt 格式，用 BCrypt 比对
        if (storedPassword.startsWith("$2a$")) {
            if (passwordEncoder.matches(password, storedPassword)) {
                return user;
            }
        }
        // 2. 兼容旧明文密码（自动升级为 BCrypt）
        else if (password.equals(storedPassword)) {
            user.setPassword(passwordEncoder.encode(password));
            userMapper.updateById(user);
            return user;
        }

        return null;
    }
}