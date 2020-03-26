### **Mybatis** **连接池与基于注解实现CRUD

####  **Mybatis** **的连接池技术**

​        在 Mybatis 的 SqlMapConfig.xml 配置文件中，通过<dataSource type="pooled">来实 现 Mybatis 中连接池的配置  

 Mybatis 将它自己的数据源分为三类：

UNPOOLED    不使用连接池的数据源

POOLED     使用连接池的数据源

JNDI 使用 实现的数据源

#### 在数据库中建立user表

```SQL
DROP TABLE IF EXISTS user;
CREATE TABLE user (
  `id` int(11) NOT NULL auto_increment,
  `username` varchar(32) NOT NULL COMMENT '用户名称',
  `birthday` datetime default NULL COMMENT '生日',
  `sex` char(1) default NULL COMMENT '性别',
  `address` varchar(256) default NULL COMMENT '地址',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert  into user(`id`,`username`,`birthday`,`sex`,`address`) values (41,'老王','2018-02-27 17:47:08','男','北京'),(42,'小二王','2018-03-02 15:09:37','女','北京金燕龙'),(43,'小二王','2018-03-04 11:34:34','女','北京金燕龙'),(45,'传智播客','2018-03-04 12:04:06','男','北京金燕龙'),(46,'老王','2018-03-07 17:37:26','男','北京'),(48,'小马宝莉','2018-03-08 11:44:00','女','北京修正');
```



#### SqlMapConfig.xml 文件中数据源的配置  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--mybatis主配置-->
<configuration>
    <properties resource="jdbcConfig.properties"></properties>
    <typeAliases>
        <package name="domain"></package>
    </typeAliases>
    <!--配置环境-->
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

    <mappers>
        <package name="dao"></package>
    </mappers>
</configuration>
```

#### 编写实体类User

```java
public class User implements Serializable {
    private Integer id;
    private String username;
    private String address;
    private String sex;
    private Date birthday;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", address='" + address + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
```

#### 编写基于注解的持久层接口IUserDao

```java
public interface IUserDao {
    @Select("select * from user")
    List<User> findAll();

    @Insert("insert into user(username,address,sex,birthday) values(#{username},#{address},#{sex},#{birthday})")
    void saveUser(User user);

    @Update("update user set username=#{username},sex=#{sex},birthday=#{birthday},address=#{address} where id=#{id}")
    void updateUser(User user);

    @Delete("delete from user where id=#{id}")
    void deleteUser(Integer userId);

    @Select("select * from user where id=#{id}")
    User findById(Integer userId);

    //@Select("select * from user where username like #{username}")
    @Select("select * from user where username like '%${value}%'")
    List<User> findUserByName(String username);

    @Select("select count(*) from user")
    int findTotalUser();
}
```

#### 编写CRUD测试类

```java
public class AnnotationCRUDTest {
    private SqlSessionFactory factory;
    private SqlSession session;
    private IUserDao userDao;
    private InputStream in;

    @Before
    public void init()throws Exception{
        in= Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        factory=new SqlSessionFactoryBuilder().build(in);
        session=factory.openSession();
        userDao=session.getMapper(IUserDao.class);
    }
    @After
    public void destroy()throws Exception{
        session.commit();
        session.close();
        in.close();
    }
    @Test
    public void TestSave(){
        User user=new User();
        user.setUsername("mybatis.annotation");
        user.setAddress("北京市昌平区");
        userDao.saveUser(user);
    }

    @Test
    public void TestUpdate(){
        User user=new User();
        user.setId(49);
        user.setUsername("mybatis.annotation.update");
        user.setAddress("北京市海淀区");
        user.setSex("男");
        user.setBirthday(new Date());
        userDao.updateUser(user);
    }

    @Test
    public void TestDelete(){
        userDao.deleteUser(42);
    }

    @Test
    public void TestFindById(){
        User user=userDao.findById(48);
        System.out.println(user);
    }

    @Test
    public void TestFindByName(){
        List<User> users=userDao.findUserByName("王");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void TestFindTotal(){
        int totalUser = userDao.findTotalUser();
        System.out.println(totalUser);
    }
}

```

#### 查询所有数据

```java
public class MybatisAnnoTest {
    public static void main(String [] args)throws Exception{
        InputStream in= Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(in);
        SqlSession session=factory.openSession();
        IUserDao userDao=session.getMapper(IUserDao.class);
        List<User> users=userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
        session.close();
        in.close();
    }
}
```

![1585216012416](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1585216012416.png)

运行结果如上

