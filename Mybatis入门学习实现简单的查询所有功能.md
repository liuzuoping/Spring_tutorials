## 什么是Mybatis 

 



mybatis 是一个优秀的基于 java 的持久层框架，它内部封装了 jdbc，使开发者只需要关注 sql 语句本身， 而不需要花费精力去处理加载驱动、创建连接、创建 statement 等繁杂的过程。

mybatis 通过 **xml 或注解**的方式将要执行的各种 statement 配置起来，并通过 java 对象和 statement 中 sql 的动态参数进行映射生成最终执行的 sql 语句，最后由 mybatis 框架执行 sql 并将结果映射为 java 对象并 返回。

采用 <u>ORM 思想解决了实体和数据库映射的问题，对 jdbc 进行了封装，屏蔽了 jdbc api 底层访问细节，使我 们不用与 jdbc api 打交道</u>，就可以完成对数据库的持久化操作。

### JDBC编程分析

```java
private String url="jdbc:mysql:///数据库名";
private String user="数据库账户名";
private String password = "数据库登录密码";
//第一步，注册驱动程序   
Class.forName("数据库驱动的完整类名"); //即com.mysql.jdbc.Driver  
//第二步，获取一个数据库的连接  
Connection conn = DriverManager.getConnection(url,user,passord);  
//第三步，创建一个会话  
Statement stmt=conn.createStatement();  
  
//第四步，执行SQL语句，增加，删除，修改记录  
stmt.executeUpdate("增加，删除，修改记录的SQL语句");  
//或者查询记录  
ResultSet rs = stmt.executeQuery("查询记录的SQL语句");  
  
//第五步，对查询的结果进行处理  
while(rs.next()){  
//对记录的操作  
}  
//第六步，关闭连接  
rs.close(); //针对的是sql为查询语句
stmt.close();  
conn.close();
```

#### JDBC问题

1、数据库链接创建、释放频繁造成系统资源浪费从而影响系统性能，如果使用数据库链接池可解决此问题。

2、Sql 语句在代码中硬编码，造成代码不易维护，实际应用 sql 变化的可能较大，sql 变动需要改变 java

代码。

3、使用Statement 向占有位符号传参数存在硬编码，因为 sql 语句的 where 条件不一定，可能 多也可能少，修改 sql 还要修改代码，系统不易维护。

4、对结果集解析存在硬编码（查询列名），sql 变化导致解析代码变化，系统不易维护，如果能将数据库记 录封装成 pojo 对象解析比较方便。



##  搭建 Mybatis 开发环境

#### 在数据库中建立user表

```mysql
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



####  环境搭建步骤

>  第一步：创建 maven 工程 
>
> 第二步：导入坐标
>
> 第三步：编写必要代码（实体类和持久层接口） 
>
> 第四步：编写 SqlMapConfig.xml 第五步：编写映射配置文件
>
> 第六步：编写测试类

###  创建 maven 工程

在 pom.xml 文件中添加 Mybatis3.4.5 的坐标，如下：

```xml
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
            <version>4.12</version>
        </dependency>
```

#### **编写** **User** **实体类**

```java
public class User  implements Serializable {
    private Integer id;
    private String username;
    private Date birthday;
    private String sex;
    private String address;

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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", birthday=" + birthday +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}

```

####  **编写持久层接口 IUserDao**  基于注解的 mybatis 使用  

```java
public interface IUserDao {
    @Select("select * from user")
    List<User> findAll();
}
```



####  编写 SqlMapConfig.xml 配置文件  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!--mybatis主配置-->
<configuration>
    <!--配置环境-->
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
        <mapper class="dao.IUserDao"/>
    </mappers>
</configuration>
```

#### 编写测试类

```java
public class MybatisTest {
    public static void main(String [] args)throws Exception{
        InputStream in=Resources.class.getResourceAsStream("/SqlMapConfig.xml");
        SqlSessionFactoryBuilder builder=new SqlSessionFactoryBuilder();
        SqlSessionFactory factory=builder.build(in);
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

![1585205744341](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1585205744341.png)

运行结果如上，实现了查询功能

​        我们发现使用 mybatis 是非常容易的一件事情，因为只需要编写 Dao 接口并且只需要编写一个 mybatis 配置文件就够了。就可以实现功能。远比我们之前的 jdbc 方便多了。