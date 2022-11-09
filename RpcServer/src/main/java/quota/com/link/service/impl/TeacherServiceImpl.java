package quota.com.link.service.impl;

import com.quota.entity.TeacherVo;
import com.quota.service.TeacherService;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Override
    public TeacherVo getTeacherDetail() {
        TeacherVo teacherVo=new TeacherVo();
        teacherVo.setAddress("杭州余杭");
        teacherVo.setAge("25");
        teacherVo.setMoney(10000.00);
        return teacherVo;
    }
}
