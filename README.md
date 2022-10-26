# jdbc-exercise

# Toby’s Spring 3 복습

로컬과 깃허브에 각각 새로운 Repository를 만들고 지금까지 배운 과정을 복습하는 시간을 가졌다.

## 1. 초난감 DAO와 Test코드

### 클래스 구성

<aside>
<img src="https://www.notion.so/icons/list_blue.svg" alt="https://www.notion.so/icons/list_blue.svg" width="40px" /> 
**com**

- dao
    - UserDao
- domain
    - User
</aside>

<aside>
<img src="https://www.notion.so/icons/list_pink.svg" alt="https://www.notion.so/icons/list_pink.svg" width="40px" /> 
**test**

- com.dao
    - UserDaoTest

</aside>

### 코드

- User
    
    ```java
    package com.domain;
    
    public class User {
        private String id;
        private String name;
        private String password;
    
        public User() {
        }
    
        public User(String id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }
    
        public String getId() {
            return id;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public String getPassword() {
            return password;
        }
    
        public void setPassword(String password) {
            this.password = password;
        }
    }
    ```
    
- UserDao
    
    ```java
    package com.dao;
    
    import com.domain.User;
    
    import java.sql.*;
    import java.util.Map;
    
    public class UserDao {
    
        public void add(User user) throws ClassNotFoundException, SQLException {
            Map<String, String> env = System.getenv();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection c = DriverManager.getConnection(env.get("DB_HOST"), env.get("DB_USER"), env.get("DB_PASSWORD"));
    
            PreparedStatement ps = c.prepareStatement("INSERT INTO users(id, name, password) VALUES(?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPassword());
    
            ps.executeUpdate();
            ps.close();
            c.close();
        }
    
        public User get(String id) throws ClassNotFoundException, SQLException {
            Map<String, String> env = System.getenv();
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection c = DriverManager.getConnection(env.get("DB_HOST"), env.get("DB_USER"), env.get("DB_PASSWORD"));
    
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setString(1, id);
    
            ResultSet rs = ps.executeQuery();
            rs.next();
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
    
            rs.close();
            ps.close();
            c.close();
    
            return user;
        }
    }
    ```
    
- UserDaoTest
    
    ```java
    package com.dao;
    
    import com.domain.User;
    import org.junit.jupiter.api.Test;
    
    import java.sql.SQLException;
    
    import static org.junit.jupiter.api.Assertions.*;
    
    class UserDaoTest {
    
        @Test
        void addAndGet() throws SQLException, ClassNotFoundException {
            UserDao userDao = new UserDao();
            User user = new User ("1", "chanmin", "1123");
            userDao.add(user);
    
            User selectedUser = userDao.get(user.getId());
            assertEquals(user.getId(), selectedUser.getId());
            assertEquals(user.getName(), selectedUser.getName());
            assertEquals(user.getPassword(), selectedUser.getPassword());
        }
    }
    ```
    

### 요약

- UserDao
    - add() 메소드가 구현되었다. add() 메소드는 mysql과 연동되어 데이터를 DB에 추가한다.
    - get() 메소드가 구현되었다. get() 메소드는 mysql과 연동되어 id를 기준으로 데이터를 불러온다.
- UserDaoTest
    - addAndGet() 테스트가 구현되었다. 새로운 데이터를 add()한 후, 실제로 그 데이터가 잘 들어갔는지 get()으로 검증한다.
