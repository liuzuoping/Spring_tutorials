##  Spring JDBC

###  **Spring** **是什么**

​         Spring 是分层的 Java SE/EE 应用 full-stack 轻量级开源框架，以 IoC（Inverse Of Control： 反转控制）和 AOP（Aspect Oriented Programming：面向切面编程）为内核，提供了展现层 Spring MVC 和持久层 Spring JDBC 以及业务层事务管理等众多的企业级应用技术，还能整合开源世界众多 著名的第三方框架和类库，逐渐成为使用最多的 Java EE 企业应用开源框架。**1.1.1**   





### **spring** **的优势**

**方便解耦，简化开发**

通过 Spring 提供的 IoC 容器，可以将对象间的依赖关系交由 Spring 进行控制，避免硬编码所造成的过度程序耦合。用户也不必再为单例模式类、属性文件解析等这些很底层的需求编写代码，可以更专注于上层的应用。

**AOP** **编程的支持**

通过 Spring 的 AOP 功能，方便进行面向切面的编程，许多不容易用传统 OOP 实现的功能可以通过AOP轻松应付。

**声明式事务的支持** 

可以将我们从单调烦闷的事务管理代码中解脱出来，通过声明式方式灵活的进行事务的管理，提高开发效率和质量。 **方便程序的测试**可以用非容器依赖的编程方式进行几乎所有的测试工作，测试不再是昂贵的操作，而是随手可做的事情。

**方便集成各种优秀框架**

Spring 可以降低各种框架的使用难度，提供了对各种优秀框架（Struts、Hibernate、Hessian、Quartz 等）的直接支持。

**降低** **JavaEE** **API** **的使用难度**

Spring 对 JavaEE API（如 JDBC、JavaMail、远程调用等）进行了薄薄的封装层，使这些 API 的 使用难度大为降低。

**Java** **源码是经典学习范例**

Spring 的源代码设计精妙、结构清晰、匠心独用，处处体现着大师对 Java 设计模式灵活运用以 及对 Java 技术的高深造诣。它的源代码无意是 Java  技术的最佳实践的范例。

![1590997048956](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1590997048956.png)

###         使用 spring 的 IOC 解决程序耦合  

​        案例的前期准备，数据库中建立account表

```sql
create table account(
	id int primary key auto_increment, 
    name varchar(40),
	money float
)character set utf8 collate utf8_general_ci;


insert into account(name,money) values('aaa',1000); 
insert into account(name,money) values('bbb',1000); 
insert into account(name,money) values('ccc',1000);
```



![1590997384680](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1590997384680.png)

此时创建一个maven项目，在pom.xml中做如下配置

```xml
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
    </dependencies>
```

在src.main.java 下新建jdbcTest.java

```java
public class jdbcTest {
    public static void main(String [] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn= DriverManager.getConnection("jdbc:mysql:///address", "xiaoliu", "960614abcd");
        PreparedStatement pstm = conn.prepareStatement("select * from account");
        ResultSet rs = pstm.executeQuery();
        while (rs.next()){
            System.out.println(rs.getString("name")+"---"+rs.getString("money"));
        }
        rs.close();
        pstm.close();
        conn.close();
    }
}
```

结果如下

```xml
aaa---29900
bbb---1800
ccc---700
test---12345
ccc---1000
ddd---2222
ddd---2222
eee---333
```

