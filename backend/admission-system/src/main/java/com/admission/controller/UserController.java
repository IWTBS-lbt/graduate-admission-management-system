package com.admission.controller;

import com.admission.common.Result;
import com.admission.config.JwtUtils;
import com.admission.dto.LoginDTO;
import com.admission.entity.User;
import com.admission.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            return Result.fail("账号或密码错误");
        }
        // 生成 JWT Token
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());

        // 清除密码后返回
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("role", user.getRole());
        userInfo.put("token", token);
        return Result.success(userInfo);
    }
}
