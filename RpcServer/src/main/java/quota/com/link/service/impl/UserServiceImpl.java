package quota.com.link.service.impl;

import com.quota.entity.UserVo;
import com.quota.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserVo getUserData() {
        UserVo userVo=new UserVo();
        userVo.setAddress("hello world");
        userVo.setName("youngxs");
        return userVo;
    }

    @Override
    public void setUserData() {

    }

    @Override
    public String getMessage(String str) {
        return "rpc数据校验,收到消息为："+str;
    }
}
