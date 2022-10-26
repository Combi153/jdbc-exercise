# jdbc-exercise

# Toby’s Spring 3 복습

지금까지 학습한 과정을 복습했다. 코드 리팩토링 과정에 대해 간단히 요약하고, 사용한 개념을 적어두었다.

## 1. 초난감 DAO와 Test코드

### 클래스 구성

**com**
- dao
    - UserDao
- domain
    - User

### 요약

- UserDao
    - add() 메소드가 구현되었다. add() 메소드는 mysql과 연동되어 데이터를 DB에 추가한다.
    - get() 메소드가 구현되었다. get() 메소드는 mysql과 연동되어 id를 기준으로 데이터를 불러온다.
- UserDaoTest
    - addAndGet() 테스트가 구현되었다. 새로운 데이터를 add()한 후, 실제로 그 데이터가 잘 들어갔는지 get()으로 검증한다.

## 2. Connection의 분리

### 클래스 구성

**com**
- dao
    - UserDao
    - ConnectionMaker
    - AwsConnectionMaker
    - LocalConnectionMaker
- domain
    - User


### 요약

- UserDao
    - add() 와 get() 메소드에서 중복되던 Connection을 분리했다.
    - Connection 방법을 생성자로 외부에서 DI할 수 있도록 했다.
- UserDaoTest
    - AwsConnectionMaker 객체를 UserDao에 전달하였다.
    - 단, Test코드에서 AwsConnectionMaker 객체를 전달하는 방법은 타당하지 않아 이후 수정된다.

## 3.  DaoFactory 추가, 제어관계 역전

### 클래스 구성

**com**
- dao
    - UserDao
    - ConnectionMaker
    - AwsConnectionMaker
    - LocalConnectionMaker
    - DaoFactory
- domain
    - User
### 요약

- DaoFactory
    - UserDao에 어떤 Connection을 주입할지 결정하는 것을 맡았다.
    - 객체 생성과 객체 주입을 메소드를 분리하였다.
- UserDaoTest
    - AwsConnectionMaker 객체를 생성해 UserDao에 전달하던 것을 지웠다.
    - DaoFactory 객체를 생성하고 이 객체를 참조해 DaoFactory 내부 메소드로 UserDao에 Connection 객체를 주입하였다

## 4. 스프링 IoC 사용

### 클래스 구성

**com**
- dao
    - UserDao
    - ConnectionMaker
    - AwsConnectionMaker
    - LocalConnectionMaker
    - DaoFactory
- domain
    - User

### 요약

- DaoFactory
    - Spring을 활용하기 위해 어노테이션을 추가했다.
    - @Configuration, @Bean을 클래스와 메소드 상단에 작성했다.
- UserDaoTest
    - Applicationcontext에서 객체를 주입받았다.
    - `UserDao userDao = context.getBean("awsUserDao", UserDao.class);` 코드를 통해 “awsUserDao” 이름의 메소드로 UserDao.class를 불러온다. 이때 UserDao는 AwsConnectionMaker의 객체를 주입받는다.

## 5. DataSource 사용

### 클래스 구성

**com**
- dao
    - UserDao
    - DaoFactory
- domain
    - User

### 개념

**DataSource**

- 자바에서 제공하는 인터페이스로 DB 커넥션을 가져오는 기능을 제공한다.
- getConnection() 메소드는 DB 커넥션을 가져온다.
- 기타 다양한 메소드가 있다.

### 요약

- DaoFactory
    - 기존의 만들었던 ConnectionMaker의 그 구현 클래스들을 사용하지 않고 DataSource를 사용하였다.
- UserDaoTest
    - 기존에 UserDao는 AwsConnectionMaker의 객체를 주입받았지만, 생성자와 멤버변수를 DataSource 객체를 주입받도록 코드를 수정하였다. getBean() 메소드에 의해 awsDataSource()이 실행되고 UserDao는 DataSource 객체를 주입받는다.
- ConnectionMaker 인터페이스와 그 구현 클래스들을 삭제하였다.

## 6. getCount(), deleteAll() 메소드 추가

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
- domain
    - User

### 개념

**@BeforeEach**

- `@BeforeEach` : 이 어노테이션을 붙이면 그 메소드는 Test 메소드가 실행되기 전에 수행된다. Test 전체 클래스가 아닌, 개별 Test 메소드를 실행하였을 때도 `@BeforeEach` 는 수행된다.

**Junit 메소드**

- assertThrows()
    - 특정 예외가 발생하였는지 확인
    - 첫 번째 인자는 확인할 예외 클래스
    - 두 번째 인자는 테스트 하려는 코드

### 요약

- UserDao
    - deleteAll(), getCount() 메소드를 추가하였다.
- UserDaoTest
    - 추가한 메소드를 검증하기 위해 Test 코드를 작성하였다.
    - @BeforeEach 메소드를 활용해 Test 코드 작성 시 사용할 객체들을  정리하였다.
    - 모든 test에서 deleteAll()을 사용해 test 때마다 DB가 비어있을 수 있도록 하였다. 이를 통해 테스트 결과가 일관적으로 나올 수 있도록 했다.
    - getUserFailure() test에는 get() 메소드에서 발생할 수 있는 예외를 테스트하였다. get() 메소드는 DB에 존재하지 않는 id를 입력받으면 EmptyResultDataAccessException 예외를 발생시킨다. assertThrows()를 통해 이러한 예외가 잘 발생하는지 테스트 할 수 있다.

## 7. 예외처리

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
- domain
    - User

### 요약

- UserDao
    - deleteAll(), getCount() 메소드에 try/catch/finally 구문을 적용해 예외처리를 해주었다.
    - 기존에는 오류 발생 시 Connection을 반환하지 못했다면, 예외처리 후 finally 구문을 통해 Connection을 반환할 수 있도록 하였다.

## 8. 전략 패턴

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
    - StatementStrategy
    - DeleteAllStatement
    - AddStatement
- domain
    - User

### 개념

**전략패턴**

> 전략패턴은 변하지 않는 어떤 맥락(Context)에서 변하는 부분(Strategy)만 따로 정의하고 기능을 수행하도록 하는 디자인 패턴이다.
> 
- 변하지 않는 맥락(context)는 jdbcContextWithStatementStrategy() 메소드에 담았다.
- 변하는 전략(Strategy)은 add(), deleteAll() 메소드 등의 기능을 수행하기 위한 쿼리문의 차이 등이다.
- add(), deleteAll() 메소드 등의 기능을 수행하기 위한 전략은 AddStatement, DeleteAllStatement 등의 클래스에 담겨 있다.
- add(), deleteAll() 메소드는 이러한 전략을 객체로 생성하고, jdbcContextWithStatementStrategy() 메소드에 전달하여, 자신의 기능을 수행한다.

### 요약

- UserDao
    - jdbcContextWithStatementStrategy() 메소드가 추가되었다. 기존에 add(), deleteAll() 메소드에서 중복되던 내용을 담고 있다.
    - add(), deleteAll() 메소드는 AddStatement, DeleteAllStatement 객체를 각각 생성하고 이 객체를 jdbcContextWithStatementStrategy()에 전달하여 호출한다.
- Strategy
    - StatementStrategy 인터페이스로 makePreparedStatement() 메소드를 갖고 있다.
    - AddStatement, DeleteAllStatement는 StatementStrategy 인터페이스의 구현 클래스이다. makePreparedStatement() 메소드를 오버라이드함으로써 add(), deleteAll() 기능에 필요한 SQL 구문과 설정을  담고 있다.
    

## 9. 템플릿 콜백 패턴 적용

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
    - StatementStrategy
- domain
    - User

### 개념

**템플릿 콜백 패턴**

토비의 스프링은 템플릿/콜백 패턴을 아래와 같이 정의한다.

> 전략 패턴의 기본 구조에 익명 내부 클래스를 활용하는 방식을 스프링에서는 템플릿/콜백 패턴이라고 부른다. 전략 패턴의 Context를 템플릿이라 부르고, 익명 내부 클래스로 만들어지는 오브젝트를 콜백이라고 부른다.
> 

핵심을 다음과 같이 정리할 수 있다.

- 템플릿 콜백 패턴은 전략 패턴의 응용이다.
- 변하지 않는 부분인 템플릿에 변하는 부분을 익명 내부 클래스(콜백 방식)로 기능하게 한다.

**템플릿 콜백의 작업 흐름**

![Untitled](1025%20%E1%84%87%E1%85%A2%E1%86%A8%E1%84%8B%E1%85%A6%E1%86%AB%E1%84%83%E1%85%B3%20%E1%84%8B%E1%85%B1%E1%84%8F%E1%85%B5%20fcc4fc5476b2439b85b6f955ceaa9c7c/Untitled%201.png)

- 클라이언트는 콜백 오브젝트를 만들고, 템플릿 메소드를 호출할 때 콜백을 전달한다.
- 템플릿은 정해진 작업 흐름 중 콜백 오브젝트의 메소드를 호출한다. 콜백은 클라이언트 메소드의 정보와 템플릿이 제공한 참조정보를 이용해서 작업을 수행하고 그 결과를 다시 템플릿에 돌려준다.
- 템플릿은 콜백이 돌려준 정보를 사용해 작업을 마친다.

> DI 방식의 전략 패턴 구조라고 생각하면 간단하다!
> 

### 요약

- UserDao
    - 기존에 AddStatement, DeleteAllStatement 클래스에 담은 내용을 add(), deleteAll() 메소드에서 익명 클래스로 담았다. 이러한 방식을 템플릿 콜백 패턴이라 부른다.
    - AddStatement, DeleteAllStatement 클래스를 삭제하여 클래스의 개수가 늘어나지 않도록 한다.

## 10. 템플릿 클래스 분리

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
    - StatementStrategy
    - JdbcContext
- domain
    - User

### 요약

- UserDao
    - 기존에 jdbcContextWithStatementStrategy() 메소드를 JdbcContext 클래스로 분리하였다.
    - UserDao는 생성자가 호출될 때 JdbcContext 객체를 생성한다.
    - JdbcContext 객체를 참조해 deleteAll() 메소드를 한 줄로 구현하였다. 코드가 굉장히 간단해졌다.
- JdbcContext
    - workWithStatementStrategy() 메소드는 기존의 jdbcContextWithStatementStrategy() 메소드의 내용을 담는다.
    - executeSql(final String query)는 query를 변하지 않는 상수로 받아 workWithStatementStrategy() 메소드에 값을 넘기며 실행한다. workWithStatementStrategy()는 넘겨받은 쿼리문을 템플릿에 맞춰 수행한다.
    

## 10. 스프링의 JdbcTemplate 적용

### 클래스 구성

**com**

- dao
    - UserDao
    - DaoFactory
- domain
    - User

### 개념

**JdbcTemplate**

- 스프링이 제공하는 JDBC 코드용 템플릿이다.
- 템플릿 콜백 패턴이 적용되어있다.
- 자주 사용되는 패턴을 가진 콜백은 템플릿에 결합시켜놓아 간단한 메소드 호출만으로 전체 기능을 사용할 수 있다.
- [메소드 확인](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html)

**JdbcTemplate.update()**

DB에서 sql을 실행한다. 다음과 같은 방식으로 활용할 수 있다.

- update(String sql)
    - 입력받은 sql을 수행한다.
- update(String sql, Object... args)
    - 입력받은 sql을 수행한다.
    - sql문에 치환자가 있다면, 함께 바인딩할 파라미터를 순서대로 입력한다.
- add(), deleteAll() 메소드는 이러한 update() 메소드를 활용하였다.

**jdbcTemplate.queryForObject()**

DB에서 id값으로 한 줄을 읽어 온다. 다음과 같은 방식으로 활용할 수 있다.

- queryForObject(String sql, Class<T> requiredType)
    - 입력받은 sql을 수행하고, requiredType으로 반환한다.
- queryForObject(String sql, RowMapper<T> rowMapper, Object... args)
    - 입력받은 sql에 치환자를 args에서 바인딩한다.
    - Resultset에서 User 객체를 생성해 반환하기 위해 RowMapper의 구현체를 입력한다.
- get(), getCount() 메소드는 이러한 queryForObject() 메소드를 활용하였다.

**********RowMapper 설명 참고**********

- [참고1](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html)
- [참고2](https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4)

### 요약

- UserDao
    - JdbcContext 객체를 참조했던 모든 메소드들을 JdbcTemplate를 사용하도록 코드를 수정하였다.
    - StatementStrategy도 사용하지 않고, JdbcTemplate 메소드들에 직접 쿼리문을 전달한다.
- JdbcContext, StatementStrategy 클래스 삭제

## 11. getAll(), getAllTest() 추가

### 클래스 구성
    
    
**com**

- dao
    - UserDao
    - DaoFactory
- domain
    - User
### 개념

**JdbcTemplate.query()**

DB에서 sql에 따라 여러 줄을 읽어온다. 다음과 같은 방식으로 활용될 수 있다.

- query(String sql, RowMapper<T> rowMapper)
    - Resultset에서 User 객체를 생성해 반환하기 위해 RowMapper의 구현체를 입력한다.
- getAll() 메소드는 query 메소드를 사용하였다.

**********RowMapper 설명 참고**********

- [참고1](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html)
- [참고2](https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4)

**********************************************멤버변수 활용 방안**********************************************

- 멤버변수로 인터페이스와 그에 대한 구현 함수를 선언할 수 있다.
- 예시 코드
    
    ```java
    public class UserDao {
    
        private DataSource dataSource;
        private JdbcTemplate jdbcTemplate;
        private RowMapper<User> userMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setName(rs.getString("name"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        };
    	...
    }
    ```
    

### 요약

- UserDao
    - 기존에 get(), getAll() 함수에 각각 구현되었던 RowMapper와 구현 메소드를 멤버변수에 선언하여 사용하였다.
    - 모든 메소드들이 한 줄로 기능되도록 작성되었다.
- UserDaoTest
    - getAll() 함수를 테스트하는 함수를 새로 작성하였다.
    - getAll() 함수는 List<User> 자료형을 반환하므로, 이를 받아서 size(), 객체 멤버변수 등을 비교하여 검증한다.
