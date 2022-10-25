package com.dao;

import com.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoFactory.class)
class UserDaoTest {

    @Autowired
    ApplicationContext context;
    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;
    @BeforeEach
    public void setUp(){
        this.userDao = context.getBean("awsUserDao", UserDao.class);
        this.user1 = new User("1", "Han", "1234");
        this.user2 = new User("2", "Kim", "4312");
        this.user3 = new User("3", "Lee", "1242");
    }

    @Test
    void addAndGet() throws SQLException, ClassNotFoundException {

        userDao.deleteAll();
        assertEquals(0, userDao.getCount());

        userDao.add(user1);
        assertEquals(1, userDao.getCount());

        userDao.add(user2);
        assertEquals(2, userDao.getCount());

        User selectedUser1 = userDao.get(user1.getId());
        assertEquals(user1.getName(), selectedUser1.getName());
        assertEquals(user1.getPassword(), selectedUser1.getPassword());

        User selectedUser2 = userDao.get(user2.getId());
        assertEquals(user2.getName(), selectedUser2.getName());
        assertEquals(user2.getPassword(), selectedUser2.getPassword());
    }

    @Test
    void count() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertEquals(0, userDao.getCount());

        userDao.add(user1);
        assertEquals(1, userDao.getCount());
        userDao.add(user2);
        assertEquals(2, userDao.getCount());
        userDao.add(user3);
        assertEquals(3, userDao.getCount());
    }

    @Test
    void getUserFailure() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertEquals(0, userDao.getCount());

        userDao.add(user1);
        assertEquals(1, userDao.getCount());

        assertThrows(EmptyResultDataAccessException.class, () -> {
                userDao.get(user2.getId());
        });
    }

    @Test
    void getAll() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        List<User> users0 = userDao.getAll();
        assertEquals(0, users0.size());

        userDao.add(user1);
        List<User> users1 = userDao.getAll();
        assertEquals(1, users1.size());
        checkSameUser(user1, users1.get(0));

        userDao.add(user2);
        List<User> users2 = userDao.getAll();
        assertEquals(2, users2.size());
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDao.add(user3);
        List<User> users3 = userDao.getAll();
        assertEquals(3, users3.size());
        checkSameUser(user1, users3.get(0));
        checkSameUser(user2, users3.get(1));
        checkSameUser(user3, users3.get(2));
    }

    private void checkSameUser(User user1, User user2){
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());
    }
}