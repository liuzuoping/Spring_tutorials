## Mybatis使用注解实现复杂关系映射开发   

​        单表的 CRUD 操作是最基本的操作，前面我们的学习都是基于 Mybaits 的映射文件来实现的以及基于注解的单表CRUD。  现在我们使用注解来实现复杂关系映射开发。

### 添加 User 实体类及 Account 实体类   

```java
public class User implements Serializable{
    private Integer userId;
    private String userName;
    private String userAddress;
    private String userSex;
    private Date userBirthday;
    //一对多关系映射：一个用户对应多个账户
    private List<Account> accounts;
    //setters&getters&toString()
}
```

###         

```java
public class Account implements Serializable {
    private Integer id;
    private Integer uid;
    private Double money;
    //多对一（mybatis中称之为一对一）的映射：一个账户只能属于一个用户
    private User user;
    //setters&getters&toString()
}
```

### 添加账户的持久层接口并使用注解配置  

```java
public interface IAccountDao {
    @Select("select * from account")
    @Results(id="accountMap",value = {
            @Result(id=true,column = "id",property = "id"),
            @Result(column = "uid",property = "uid"),
            @Result(column = "money",property = "money"),
            @Result(property = "user",column = "uid",one=@One(select="com.itheima.dao.IUserDao.findById",fetchType= FetchType.EAGER))
    })
    List<Account> findAll();

    @Select("select * from account where uid = #{userId}")
    List<Account> findAccountByUid(Integer userId);
}
```

### 添加用户的持久层接口并使用注解配置 

```java
@CacheNamespace(blocking = true)
public interface IUserDao {
    @Select("select * from user")
    @Results(id="userMap",value={
            @Result(id=true,column = "id",property = "userId"),
            @Result(column = "username",property = "userName"),
            @Result(column = "address",property = "userAddress"),
            @Result(column = "sex",property = "userSex"),
            @Result(column = "birthday",property = "userBirthday"),
            @Result(property = "accounts",column = "id",
                    many = @Many(select = "com.itheima.dao.IAccountDao.findAccountByUid",
                                fetchType = FetchType.LAZY))
    })
    List<User> findAll();

    @Select("select * from user  where id=#{id} ")
    @ResultMap("userMap")
    User findById(Integer userId);

    @Select("select * from user where username like #{username} ")
    @ResultMap("userMap")
    List<User> findUserByName(String username);
}
```

resources包下添加SqlMapConfig.xml以及jdbcConfig.properties

```xml
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql:///ssm
jdbc.username=xiaoliu
jdbc.password=960614abcd
```



```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 引入外部配置文件-->
    <properties resource="jdbcConfig.properties"></properties>
    <!--配置开启二级缓存-->
    <settings>
        <setting name="cacheEnabled" value="true"/>
    </settings>

    <!--配置别名-->
    <typeAliases>
        <package name="com.itheima.domain"></package>
    </typeAliases>
    <!-- 配置环境-->
    <environments default="mysql">
        <environment id="mysql">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="${jdbc.driver}"></property>
                <property name="url" value="${jdbc.url}"></property>
                <property name="username" value="${jdbc.username}"></property>
                <property name="password" value="${jdbc.password}"></property>
            </dataSource>
        </environment>
    </environments>
    <!-- 指定带有注解的dao接口所在位置 -->
    <mappers>
       <package name="com.itheima.dao"></package>
    </mappers>
</configuration>
```

### **测试二级缓存**  

```java
public class SecondLevelCatchTest {
    private InputStream in;
    private SqlSessionFactory factory;

    @Before
    public  void init()throws Exception{
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = new SqlSessionFactoryBuilder().build(in);
    }
    @After
    public  void destroy()throws  Exception{
        in.close();
    }
    @Test
    public void testFindOne(){
        SqlSession session = factory.openSession();
        IUserDao userDao = session.getMapper(IUserDao.class);
        User user = userDao.findById(45);
        System.out.println(user);
        session.close();//释放一级缓存
        SqlSession session1 = factory.openSession();//再次打开session
        IUserDao userDao1 = session1.getMapper(IUserDao.class);
        User user1 = userDao1.findById(45);
        System.out.println(user1);
        session1.close();
    }
}
```

### 添加账户的测试类

```java
public class AccountTest {
    private InputStream in;
    private SqlSessionFactory factory;
    private SqlSession session;
    private IAccountDao accountDao;

    @Before
    public  void init()throws Exception{
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = new SqlSessionFactoryBuilder().build(in);
        session = factory.openSession();
        accountDao = session.getMapper(IAccountDao.class);
    }

    @After
    public  void destroy()throws  Exception{
        session.commit();
        session.close();
        in.close();
    }

    @Test
    public  void  testFindAll(){
        List<Account> accounts = accountDao.findAll();
        for(Account account : accounts){
            System.out.println("----每个账户的信息-----");
            System.out.println(account);
            System.out.println(account.getUser());
        }
    }
}
```

### 添加用户CRUD的测试类

```java
public class AnnotationCRUDTest {
    private InputStream in;
    private SqlSessionFactory factory;
    private SqlSession session;
    private IUserDao userDao;

    @Before
    public  void init()throws Exception{
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        factory = new SqlSessionFactoryBuilder().build(in);
        session = factory.openSession();
        userDao = session.getMapper(IUserDao.class);
    }

    @After
    public  void destroy()throws  Exception{
        session.commit();
        session.close();
        in.close();
    }
    
    @Test
    public  void  testFindAll(){
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println("---每个用户的信息----");
            System.out.println(user);
            System.out.println(user.getAccounts());
        }
    }
    @Test
    public void testFindOne(){
        User user = userDao.findById(45);
        System.out.println(user);
    }
    @Test
    public  void testFindByName(){
        List<User> users = userDao.findUserByName("%王%");
        for(User user : users){
            System.out.println(user);
        }
    }
}
```

