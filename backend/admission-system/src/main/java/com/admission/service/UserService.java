package com.admission.service;

import com.admission.entity.User;

public interface UserService {
    User login(String username, String password);
}