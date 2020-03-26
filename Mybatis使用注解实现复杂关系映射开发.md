###         Mybatis使用注解实现复杂关系映射开发  

实现复杂关系映射之前我们可以在映射文件中通过配置<resultMap>来实现，在使用注解开发时我们需要借 助@Results 注解，@Result 注解，@One 注解，@Many 注解。

**@Results** **注解 代替的是标签******

该注解中可以使用单个@Result 注解，也可以使用@Result 集合

@Results（{@Result（），@Result（）}）或@Results（@Result（））

 

@Resutl 注解

**代替了** ******标签和********标签**

**@Result** **中 属性介绍：** id 是否是主键字段 column 数据库的列名

property 需要装配的属性名

one 需要使用的@One 注解（@Result（one=@One）（）））

many 需要使用的@Many 注解（@Result（many=@many）（）））

 

**@One** **注解（一对一） 代替了********标签，是多表查询的关键，在注解中用来指定子查询返回单一对象。**

**@One** **注解属性介绍：**

**select** **指定用来多表查询的** **sqlmapper**

fetchType 会覆盖全局的配置参数 lazyLoadingEnabled。。 使用格式：

@Result(column=" ",property="",one=@One(select=""))

 

**@Many** **注解（多对一）**

**代替了****<**Collection**>****标签****,****是是多表查询的关键，在注解中用来指定子查询返回对象集合。**

​    注意：聚集元素用来处理“一对多”的关系。需要指定映射的 Java 实体类的属性，属性的 javaType

（一般为 ArrayList）但是注解中可以不定义； 使用格式：

@Result(property="",column="",many=@Many(select=""))

###         添加 User 实体类

```java
public class User implements Serializable {
    private Integer userid;
    private String userName;
    private String userAddress;
    private String userSex;
    private Date userBirthday;
    private List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "userid=" + userid +
                ", userName='" + userName + '\'' +
                ", userAddress='" + userAddress + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userBirthday=" + userBirthday +
                '}';
    }
}

```

### 添加  Account 实体类 

```java
public class Account implements Serializable {
    private Integer id;
    private Integer uid;
    private Double money;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", uid=" + uid +
                ", money=" + money +
                '}';
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}

```

#### 编写SQL语句

```mysql
SELECT
account.*, user.username, user.address
FROM
account, user
WHERE account.uid = user.id
```

#### 定义持久层接口

```java
@CacheNamespace(blocking = true)
public interface  IUserDao {
    @Select("select * from user")
    @Results(id = "userMap", value = {
            @Result(id = true,column = "id",property = "userid"),
            @Result(column = "username",property = "userName"),
            @Result(column = "address",property = "userAddress"),
            @Result(column = "sex",property = "userSex"),
            @Result(column = "birthday",property = "userBirthday"),
            @Result(property = "accounts", column = "id",
                    many = @Many(select = "dao.IAccountDao.findAccountByUid",fetchType = FetchType.LAZY))
    })
    List<User> findAll();


    @Select("select * from user where id=#{id}")
    @ResultMap(value = {"userMap"})
    User findById(Integer userId);

    @Select("select * from user where username like #{username}")
    @ResultMap("userMap")
    List<User> findUserByName(String username);
}
```



```java
public interface IAccountDao {
    @Select("select * from account")
    @Results(id = "accountMap", value={
            @Result(id = true,column = "id",property = "id"),
            @Result(column = "uid",property = "uid"),
            @Result(column = "money",property = "money"),
            @Result(property = "user",column = "uid",
                    one = @One(select="dao.IUserDao.findById",fetchType= FetchType.EAGER))
    })
    List<Account> findAll();

    @Select("select * from account where uid=#{userId}")
    List<Account> findAccountByUid(Integer userId);
}

```

#### 编写测试类

```java
public class AccountTest {
    private SqlSessionFactory factory;
    private SqlSession session;
    private IAccountDao accountDao;
    private InputStream in;

    @Before
    public void init()throws Exception{
        in= Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        factory=new SqlSessionFactoryBuilder().build(in);
        session=factory.openSession();
        accountDao=session.getMapper(IAccountDao.class);
    }
    @After
    public void destroy()throws Exception{
        session.commit();
        session.close();
        in.close();
    }


    @Test
    public void testFindAll(){
        List<Account> accounts=accountDao.findAll();
        for (Account account : accounts) {
            System.out.println("---");
            System.out.println(account);
            System.out.println(account.getUser());
        }
    }

}

```

