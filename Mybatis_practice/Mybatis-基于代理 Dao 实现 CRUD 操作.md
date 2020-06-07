## 基于代理 Dao 实现 CRUD 操作

###         \#{}与${}的区别  

**#{}表示一个占位符号**

通过#{}可以实现 preparedStatement 向占位符中设置值，自动进行 java 类型和 jdbc 类型转换，

<u>\#{}可以有效防止 sql 注入。</u> #{}可以接收简单类型值或 pojo 属性值。 如果 parameterType 传输单个简单类型值，#{}括号中可以是 value 或其它名称。

**${}表示拼接 sql 串**

通过${}可以将 parameterType 传入的内容拼接在 sql 中且不进行 jdbc 类型转换， ${}可以接收简单类型值或 pojo 属性值，如果 parameterType 传输单个简单类型值，${}括号中只能是 value。

###  创建 maven 工程

pom.xml中导入以下坐标

```xml
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.4.5</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
        <dependency>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
            <version>1.2.12</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.10</version>
        </dependency>
    </dependencies>
```

**编写** **User** **实体类**

```java
public class User implements Serializable{
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;
    //getters&setters&toString()
```

​      

**编写** **QueryVo** **实体类**

```java
public class QueryVo {
    private User user;
    //getters&setters
}
```

  **编写持久层接口 IUserDao**  

```java
public interface IUserDao {
    List<User> findAll();

    void saveUser(User user);

    void updateUser(User user);

    void deleteUser(Integer userId);

    User findById(Integer userId);

    List<User> findByName(String username);

    int findTotal();

    List<User> findUserByVo(QueryVo vo);
}
```

### **在用户的映射配置文件中配置**

在resources包下建立和IUserDao一样的文件结构，新建IUserDao.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.itheima.dao.IUserDao">

    <!-- 配置 查询结果的列名和实体类的属性名的对应关系 -->
    <resultMap id="userMap" type="user">
        <!-- 主键字段的对应 -->
        <id property="userId" column="id"></id>
        <!--非主键字段的对应-->
        <result property="userName" column="username"></result>
        <result property="userAddress" column="address"></result>
        <result property="userSex" column="sex"></result>
        <result property="userBirthday" column="birthday"></result>
    </resultMap>
    
    <!-- 查询所有 -->
    <select id="findAll" resultMap="userMap">
        <!--select id as userId,username as userName,address as userAddress,sex as userSex,birthday as userBirthday from user;-->
        select * from user;
    </select>

    <!-- 保存用户 -->
    <insert id="saveUser" parameterType="user">
        <!-- 配置插入操作后，获取插入数据的id -->
        <selectKey keyProperty="userId" keyColumn="id" resultType="int" order="AFTER">
            select last_insert_id();
        </selectKey>
        insert into user(username,address,sex,birthday)values(#{userName},#{userAddress},#{userSex},#{userBirthday});
    </insert>

    <!-- 更新用户 -->
    <update id="updateUser" parameterType="USER">
        update user set username=#{userName},address=#{userAddress},sex=#{userSex},birthday=#{userBirthday} where id=#{userId}
    </update>

    <!-- 删除用户-->
    <delete id="deleteUser" parameterType="java.lang.Integer">
        delete from user where id = #{uid}
    </delete>

    <!-- 根据id查询用户 -->
    <select id="findById" parameterType="INT" resultMap="userMap">
        select * from user where id = #{uid}
    </select>

    <!-- 根据名称模糊查询 -->
    <select id="findByName" parameterType="string" resultMap="userMap">
          select * from user where username like #{name}
        <!-- select * from user where username like '%${value}%'-->
   </select>

    <!-- 获取用户的总记录条数 -->
    <select id="findTotal" resultType="int">
        select count(id) from user;
    </select>

    <!-- 根据queryVo的条件查询用户 -->
    <select id="findUserByVo" parameterType="com.itheima.domain.QueryVo" resultMap="userMap">
        select * from user where username like #{user.userName}
    </select>
</mapper>         
```

###         SqlMapConfig.xml配置文件  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>

    //在前面我们讲的 Mybatis 支持的默认别名，我们也可以采用自定义别名方式来开发。
    <typeAliases>
        <!-- 批量别名定义，扫描整个包下的类，别名为类名（首字母大写或小写都可以） -->
        <package name="com.itheima.domain"></package>
    </typeAliases>

    <!--配置环境-->
    <environments default="mysql">
        <!-- 配置mysql的环境-->
        <environment id="mysql">
            <!-- 配置事务 -->
            <transactionManager type="JDBC"></transactionManager>
            <!--配置连接池-->
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"></property>
                <property name="url" value="jdbc:mysql:///address"></property>
                <property name="username" value="xiaoliu"></property>
                <property name="password" value="960614abcd"></property>
            </dataSource>
        </environment>
    </environments>
    <!-- 配置映射文件的位置 -->
    <mappers>
        <!--<mapper resource="com/itheima/dao/IUserDao.xml"></mapper>-->
        <!-- package标签是用于指定dao接口所在的包,当指定了之后就不需要在写mapper以及resource或者class了 -->
        <package name="com.itheima.dao"></package>
    </mappers>
</configuration>
```

###     在测试类添加测试  

```java
public class MybatisTest {
    private InputStream in;
    private SqlSession sqlSession;
    private IUserDao userDao;
    @Before//用于在测试方法执行之前执行
    public void init()throws Exception{
        //1.读取配置文件，生成字节输入流
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.获取SqlSessionFactory
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(in);
        //3.获取SqlSession对象
        sqlSession = factory.openSession();
        //4.获取dao的代理对象
        userDao = sqlSession.getMapper(IUserDao.class);
    }

    @After//用于在测试方法执行之后执行
    public void destroy()throws Exception{
        sqlSession.commit();
        sqlSession.close();
        in.close();
    }

    @Test
    public void testFindAll(){
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println(user);
        }

    }

    @Test
    public void testSave(){
        User user = new User();
        user.setUserName("张婉");
        user.setUserAddress("上海市松江区");
        user.setUserSex("女");
        user.setUserBirthday(new Date());
        System.out.println("保存操作之前："+user);
        userDao.saveUser(user);
        System.out.println("保存操作之后："+user);
    }

    @Test
    public void testUpdate(){
        User user = new User();
        user.setUserId(50);
        user.setUserName("刘伟");
        user.setUserAddress("湖南省韶山市");
        user.setUserSex("男");
        user.setUserBirthday(new Date());
        userDao.updateUser(user);
    }

    @Test
    public void testDelete(){
        userDao.deleteUser(49);
    }

    @Test
    public void testFindOne(){
        User  user = userDao.findById(50);
        System.out.println(user);
    }

    @Test
    public void testFindByName(){
        List<User> users = userDao.findByName("%王%");
//        List<User> users = userDao.findByName("王");
        for(User user : users){
            System.out.println(user);
        }
    }

    @Test
    public void testFindTotal(){
        int count = userDao.findTotal();
        System.out.println(count);
    }

    @Test
    public void testFindByVo(){
        QueryVo vo = new QueryVo();
        User user = new User();
        user.setUserName("%王%");
        vo.setUser(user);
        //5.执行查询一个方法
        List<User> users = userDao.findUserByVo(vo);
        for(User u : users){
            System.out.println(u);
        }
    }
}
```

文件结构如下

![1591439955685](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591439955685.png)

还可以在resources目录下建立jdbcConfig.properties

```xml
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/eesy_mybatis
jdbc.username=root
jdbc.password=1234
```

此时SqlMapConfig.xml变为

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置properties-->
    <properties resource="jdbcConfig.properties"></properties>

    <!--使用typeAliases配置别名，它只能配置domain中类的别名 -->
    <typeAliases>
        <package name="com.itheima.domain"></package>
    </typeAliases>

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
        <package name="com.itheima.dao"></package>
    </mappers>
</configuration>
```

##         **动态 SQL**  

###         持久层 Dao 映射配置  

IUserDao.xml中加入以下代码

```xml
    <select id="findUserByCondition" resultMap="userMap" parameterType="user">
        select * from user
        <where>
            <if test="userName != null">
                and username = #{userName}
            </if>
            <if test="userSex != null">
                and sex = #{userSex}
            </if>
        </where>
    </select>

    <select id="findUserInIds" resultMap="userMap" parameterType="queryvo">
        <include refid="defaultUser"></include>
        <where>
            <if test="ids != null and ids.size()>0">
                <foreach collection="ids" open="and id in (" close=")" item="uid" separator=",">
                    #{uid}
                </foreach>
            </if>
        </where>
    </select>
```

IUserDao.java中加入以下代码

```java
    List<User> findUserByCondition(User user);
    List<User> findUserInIds(QueryVo vo);
```

QueryVo.java修改为

```java
public class QueryVo {
    private User user;
    private List<Integer> ids;
    //getters&setters
}
```

测试类中加入以下代码

```java
    @Test
    public void testFindByCondition(){
        User u = new User();
        u.setUserName("老王");
        List<User> users = userDao.findUserByCondition(u);
        for(User user : users){
            System.out.println(user);
        }
    }

    @Test
    public void testFindInIds(){
        QueryVo vo = new QueryVo();
        List<Integer> list = new ArrayList<Integer>();
        list.add(41);
        list.add(43);
        list.add(46);
        vo.setIds(list);
        List<User> users = userDao.findUserInIds(vo);
        for(User user : users){
            System.out.println(user);
        }
```

