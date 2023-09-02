package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;

public interface UserService {
    User updateUser(User user);
    //    Admin updateAdmin (AdminDto adminDto);
    String removeUser(Integer userId);
    // buy tickets
}
