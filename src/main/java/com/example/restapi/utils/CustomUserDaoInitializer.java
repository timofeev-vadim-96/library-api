package com.example.restapi.utils;

import com.example.restapi.dao.CustomUserDao;
import com.example.restapi.models.security.CustomUserEntity;
import com.example.restapi.security.commonConfig.CustomPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("security_common")
public class CustomUserDaoInitializer {
    private final CustomUserDao userDao;
    private final CustomPasswordEncoder encoder;

    @EventListener(ContextRefreshedEvent.class)
    private void init(){
        if (userDao.findAll().isEmpty()) {
            CustomUserEntity admin = new CustomUserEntity("admin", encoder.encode("admin"), "admin");
            CustomUserEntity manager = new CustomUserEntity("manager", encoder.encode("manager"), "manager");
            CustomUserEntity user = new CustomUserEntity("user", encoder.encode("user"), "user");
            userDao.save(admin);
            userDao.save(manager);
            userDao.save(user);
        }
    }
}
