## 搭建 Mybatis 开发环境

###   创建 maven 工程

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

​        **编写持久层接口 IUserDao**  

```java
public interface IUserDao {
    List<User> findAll();
}
```

**resources目录下建立与IUserDao相同的文件结构编写持久层接口的映射文件** **IUserDao.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.itheima.dao.IUserDao">
    <!--配置查询所有-->
    <select id="findAll" resultType="com.itheima.domain.User">
        select * from user
    </select>
</mapper>
```

####        resources目录下 编写 SqlMapConfig.xml 配置文件  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!-- mybatis的主配置文件 -->
<configuration>
    <environments default="mysql">
        <environment id="mysql">
            <transactionManager type="JDBC"></transactionManager>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql:///address"/>
                <property name="username" value="xiaoliu"/>
                <property name="password" value="960614abcd"/>
            </dataSource>
        </environment>
    </environments>

    <mappers>
        <mapper resource="com/itxiaoliu/dao/IUserDao.xml"/>
    </mappers>
</configuration>
```

#### test包下编写测试类

```java
public class MybatisTest {
    public static void main(String[] args)throws Exception {
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        SqlSession session = factory.openSession();
        IUserDao userDao = session.getMapper(IUserDao.class);
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println(user);
        }
        session.close();
        in.close();
    }
}
```

![1591429889816](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591429889816.png)

##      基于注解的 mybatis 使用  

###         在持久层接口中添加注解  

```java
public interface IUserDao {
    @Select("select * from user")
    List<User> findAll();
}
```

### **修改** **SqlMapConfig.xml**

```xml
    <!-- 指定映射配置文件的位置，映射配置文件指的是每个dao独立的配置文件
        如果是用注解来配置的话，此处应该使用class属性指定被注解的dao全限定类名
    -->
    <mappers>
        <mapper class="com.itheima.dao.IUserDao"/>
    </mappers>
```

