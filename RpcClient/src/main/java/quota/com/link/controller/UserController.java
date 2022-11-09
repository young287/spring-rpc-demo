package quota.com.link.controller;

import com.quota.entity.TeacherVo;
import com.quota.entity.UserVo;
import com.quota.service.LionService;
import com.quota.service.TeacherService;
import com.quota.service.UserService;
import org.omg.CORBA.portable.ValueOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {


    @Autowired
    UserService userService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    LionService lionService;

    @GetMapping("/user")
    public void user(){
        UserVo userData = userService.getUserData();
        System.out.println(userData);
    }

    @GetMapping("/teacher")
    public void teacher(){
         TeacherVo teacherDetail = teacherService.getTeacherDetail();
        System.out.println(teacherDetail);
    }

    @GetMapping("/lion")
    public void lion(){
         String tiger = lionService.setLionName("tiger");
        System.out.println(tiger);
    }
}
