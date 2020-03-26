# Mybatis 传统 DAO 层实现CRUD

 

## Mybatis 实现 DAO 的传统开发方式

#### 实体类User

根据数据库中的属性编写代码并且生成其getter、setter与toString方法

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



#### 持久层Dao接口

```java
public interface IUserDao {
    List<User> findAll();
    void saveUser(User user);
    void updateUser(User user);
    void deleteUser(Integer userId);
    User findById(Integer userId);
    List<User> findByName(String username);
    int findTotal();
}
```

#### **持久层** **Dao** **实现类**

```java
public class UserDaoImpl implements IUserDao {
    private SqlSessionFactory factory;
    public UserDaoImpl(SqlSessionFactory factory){
        this.factory=factory;
    }

    public List<User> findAll() {
        SqlSession session=factory.openSession();
        List<User> users = session.selectList("dao.IUserDao.findAll");
        session.close();
        return users;
    }

    public void saveUser(User user) {
        SqlSession session=factory.openSession();
        session.insert("dao.IUserDao.saveUser",user);
        session.commit();
        session.close();
    }

    public void updateUser(User user) {
        SqlSession session=factory.openSession();
        session.update("dao.IUserDao.updateUser",user);
        session.commit();
        session.close();
    }

    public void deleteUser(Integer userId) {
        SqlSession session=factory.openSession();
        session.delete("dao.IUserDao.deleteUser",userId);
        session.commit();
        session.close();
    }

    public User findById(Integer userId) {
        SqlSession session=factory.openSession();
        User user = session.selectOne("dao.IUserDao.findById",userId);
        session.close();
        return user;
    }

    public List<User> findByName(String username) {
        SqlSession session=factory.openSession();
        List<User> users = session.selectList("dao.IUserDao.findByName",username);
        session.close();
        return users;
    }

    public int findTotal() {
        SqlSession session=factory.openSession();
        Integer count = session.selectOne("dao.IUserDao.findTotal");
        session.close();
        return count;
    }
}
```

#### 编写测试类

```java
public class MybatisTest {
    private InputStream in;
    private IUserDao userDao;
    @Before
    public void init()throws Exception{
        in= Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        SqlSessionFactory factory=new SqlSessionFactoryBuilder().build(in);
        userDao=new UserDaoImpl(factory);
    }
    @After
    public void destroy()throws Exception{
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
        user.setUsername("dao impl user");
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
        user.setId(30);
        user.setUsername("mybatis.update");
        user.setAddress("北京市顺义区");
        user.setSex("女");
        user.setBirthday(new Date());
        userDao.updateUser(user);
    }

    @Test
    public void testDelete() {
        userDao.deleteUser(30);
    }

    @Test
    public void testFindone() {
        User user=userDao.findById(28);
        System.out.println(user);
    }

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

}
```

