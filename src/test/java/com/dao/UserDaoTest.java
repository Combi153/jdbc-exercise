package com.dao;

import com.domain.User;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDaoTest {

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {
        UserDao userDao = new DaoFactory().awsUserDao();
        User user = new User ("1", "chanmin", "1123");
        userDao.add(user);

        User selectedUser = userDao.get(user.getId());
        assertEquals(user.getId(), selectedUser.getId());
        assertEquals(user.getName(), selectedUser.getName());
        assertEquals(user.getPassword(), selectedUser.getPassword());
    }
}