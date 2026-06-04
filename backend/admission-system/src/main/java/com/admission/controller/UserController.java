package com.admission.controller;

import com.admission.common.Result;
import com.admission.entity.User;
import com.admission.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(String username, String password) {
        User user = userService.login(username, password);
        if(user == null){
            return Result.fail("账号或密码错误");
        }
        return Result.success(user);
    }
}