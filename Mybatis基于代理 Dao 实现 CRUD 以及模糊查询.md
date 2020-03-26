###         Mybatis**基于代理 Dao 实现 CRUD 操作**  

#### 编写实体类User

```java
public class User implements Serializable {
    private Integer id;
    private String username;
    private String address;
    private String sex;
    private Date birthday;

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
}

```



#### **在持久层IUserDao接口中添加**CRUD **方法**

```java
public interface IUserDao {
    List<User> findAll();
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Integer userId);
    User findById(Integer userId);
```

#### **在用户的映射配置文件IUserDao.xml中配置**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="dao.IUserDao">
    <select id="findAll" resultType="domain.User">
        select * from user;
    </select>
    <insert id="saveUser" parameterType="domain.User">
        <selectKey keyProperty="id" keyColumn="id" resultType="int" order="AFTER">
            select last_insert_id();
        </selectKey>
        insert into user (username,address,sex,birthday)values(#{username},#{address},#{sex},#{birthday});
    </insert>
    <update id="updateUser" parameterType="domain.User">
        update user set username=#{username},address=#{address},sex=#{sex},birthday=#{birthday} where id=#{id};
    </update>
    <delete id="deleteUser" parameterType="Integer">
        delete from user where id=#{id};
    </delete>
    <select id="findById" parameterType="INT" resultType="domain.User">
        select * from user where id=#{id};
    </select>
```

#### 添加测试类中的测试方法  

```java
public class MybatisTest {
    private InputStream in;
    private SqlSession sqlSession;
    private IUserDao userDao;
    @Before
    public void init()throws Exception{
        in= Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(in);
        sqlSession=factory.openSession();
        userDao=sqlSession.getMapper(IUserDao.class);
    }
    @After
    public void destroy()throws Exception{
        sqlSession.commit();
        sqlSession.close();
        in.close();
    }
    @Test
    public void testFindAll(){
        List<User> users=userDao.findAll();
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testSave(){
        User user=new User();
        user.setUsername("mybatis last insertid");
        user.setAddress("北京市顺义区");
        user.setSex("男");
        user.setBirthday(new Date());
        System.out.println("保存操作之前"+user);
        userDao.saveUser(user);
        System.out.println("保存操作之后"+user);
    }

    @Test
    public void testUpdate(){
        User user=new User();
        user.setId(10);
        user.setUsername("mybatis.update");
        user.setAddress("北京市顺义区");
        user.setSex("女");
        user.setBirthday(new Date());
        userDao.updateUser(user);
    }

    @Test
    public void testDelete() {
        userDao.deleteUser(10);
    }

    @Test
    public void testFindone() {
        User user=userDao.findById(28);
        System.out.println(user);
    }
```

基 本 类 型 和 String 我 们 可 以 直 接 写 类 型 名 称 ， 也 可 以 使 用 包 名 . 类 名 的 方 式 ， 例 如 ：

java.lang.String。

实体类类型，目前我们只能使用全限定类名。

#### 编写 QueryVo 实体类

```java
public class QueryVo {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
```

#### 在持久层接口中加入代码

```java
    List<User> findByName(String username);
    int findTotal();
    List<User> findUserByVo(QueryVo vo);
```

#### 在配置文件中添加以下配置

```xml
    <select id="findByName" parameterType="string" resultType="domain.User">
        select * from user where username like #{username};
    </select>
    <select id="findTotal" resultType="int">
        select count(id) from user;
    </select>
    <select id="findUserByVo" parameterType="domain.QueryVo" resultType="domain.User">
        select * from user where username like #{user.username};
    </select>
```

#### **测试包装类作为参数**

```java
    @Test
    public void testFindByName() {
        List<User> users=userDao.findByName("%王%");
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test
    public void testFindTotal() {
        int count=userDao.findTotal();
        System.out.println(count);
    }

    @Test
    public void testFindByVo() {
        QueryVo vo = new QueryVo();
        User user=new User();
        user.setUsername("%王%");
        List<User> users=userDao.findUserByVo(vo);
        for (User u : users) {
            System.out.println(u);
        }
    }
```

