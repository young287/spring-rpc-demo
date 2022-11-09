package com.quota.service;

import com.quota.entity.UserVo;

public interface UserService {
     UserVo getUserData();

     void  setUserData();

     String getMessage(String str);

}
