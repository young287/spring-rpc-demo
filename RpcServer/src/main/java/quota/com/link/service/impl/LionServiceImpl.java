package quota.com.link.service.impl;

import com.quota.service.LionService;
import org.springframework.stereotype.Service;

@Service
public class LionServiceImpl implements LionService {
    @Override
    public String setLionName(String name) {
        return "Lion's Name is "+name;
    }
}
