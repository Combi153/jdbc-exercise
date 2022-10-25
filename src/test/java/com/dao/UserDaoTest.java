package com.dao;

import com.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
class UserDaoTest {

    @Autowired
    ApplicationContext context;

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        UserDao userDao = context.getBean("awsUserDao", UserDao.class);
        User user = new User ("1", "chanmin", "1123");
        userDao.add(user);

        User selectedUser = userDao.get(user.getId());
        assertEquals(user.getId(), selectedUser.getId());
        assertEquals(user.getName(), selectedUser.getName());
        assertEquals(user.getPassword(), selectedUser.getPassword());
    }
}