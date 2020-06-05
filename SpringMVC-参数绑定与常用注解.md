## 1 快速创建SpringMVC项目

![1591270169326](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591270169326.png)



添加键值对**archetypeCatalog:internal**解决创建项目过慢问题



![1591270232888](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591270232888.png)

pom.xml中加入以下依赖

```xml
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <spring.version>5.0.2.RELEASE</spring.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-context</artifactId>
      <version>${spring.version}</version>
    </dependency>

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>${spring.version}</version>
    </dependency>

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-webmvc</artifactId>
      <version>${spring.version}</version>
    </dependency>

    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
      <version>2.5</version>
      <scope>provided</scope>
    </dependency>

    <dependency>
      <groupId>javax.servlet.jsp</groupId>
      <artifactId>jsp-api</artifactId>
      <version>2.0</version>
      <scope>provided</scope>
    </dependency>
  </dependencies>
```

main目录下新建java包与resources包

resources包下新建Springmvc.xml并引入以下名称空间

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
</beans>
```

web.xml中配置前端控制器

```xml
  <servlet>
    <servlet-name>dispatcherServlet</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:Springmvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>dispatcherServlet</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
```

在java包下新建cn.itxiaoliu.controller.HelloController.java

```java
@Controller
public class HelloController {
    @RequestMapping(path = "/hello")
    public String sayHello(){
        System.out.println("Hello SpringMVC");
        return "success";
    }
}
```

WEB-INF文件夹下新建pages.success.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h3>入门成功</h3>
</body>
</html>
```

webapp目录下新建index.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h3>入门程序</h3>
    <a href="hello">入门程序</a>
</body>
</html>
```



在Springmvc.xml中开启注解扫描、配置视图解析器并开启SpringMVC对注解的支持

```xml
    <context:component-scan base-package="cn.itxiaoliu"/>
    <bean id="InternalResourceViewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/pages/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
	<mvc:annotation-driven/>
```

配置好tomcat并启动项目便可实现程序



##         **请求参数的绑定**  

java包下新建domain.User.java

```java
public class User implements Serializable{
    private String uname;
    private Integer age;
    private Date date;
    //setters&getters&toStrings()
```

以及domain.Account.java

```java
public class Account implements Serializable{
    private String username;
    private String password;
    private Double money;
    private User user;
    private List<User> list;
    private Map<String,User> map;
    //setters&getters&toStrings()
```

**controller包下新建ParamController.java**

```java
@Controller
@RequestMapping("/param")
public class ParamController {

    /**
     * 请求参数绑定入门
     * @return
     */
    @RequestMapping("/testParam")
    public String testParam(String username,String password){
        System.out.println("执行了...");
        System.out.println("用户名："+username);
        System.out.println("密码："+password);
        return "success";
    }

    /**
     * 请求参数绑定把数据封装到JavaBean的类中
     * @return
     */
    @RequestMapping("/saveAccount")
    public String saveAccount(Account account){
        System.out.println("执行了...");
        System.out.println(account);
        return "success";
    }

    /**
     * 自定义类型转换器
     * @param user
     * @return
     */
    @RequestMapping("/saveUser")
    public String saveUser(User user){
        System.out.println("执行了...");
        System.out.println(user);
        return "success";
    }

    /**
     * 原生的API
     * @return
     */
    @RequestMapping("/testServlet")
    public String testServlet(HttpServletRequest request, HttpServletResponse response){
        System.out.println("执行了...");
        System.out.println(request);
        HttpSession session = request.getSession();
        System.out.println(session);
        ServletContext servletContext = session.getServletContext();
        System.out.println(servletContext);
        System.out.println(response);
        return "success";
    }
}
```

webapp包下新建param.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%--请求参数绑定--%>
    <a href="param/testParam?username=hehe&password=123">请求参数绑定</a>

    <form action="param/saveAccount" method="post">
        姓名：<input type="text" name="username" /><br/>
        密码：<input type="text" name="password" /><br/>
        金额：<input type="text" name="money" /><br/>
        用户姓名：<input type="text" name="user.uname" /><br/>
        用户年龄：<input type="text" name="user.age" /><br/>
        <input type="submit" value="提交" />
    </form>

    <form action="param/saveAccount" method="post">
        姓名：<input type="text" name="username" /><br/>
        密码：<input type="text" name="password" /><br/>
        金额：<input type="text" name="money" /><br/>
        用户姓名：<input type="text" name="list[0].uname" /><br/>
        用户年龄：<input type="text" name="list[0].age" /><br/>
        用户姓名：<input type="text" name="map['one'].uname" /><br/>
        用户年龄：<input type="text" name="map['one'].age" /><br/>
        <input type="submit" value="提交" />
    </form>

    <form action="param/saveUser" method="post">
        用户姓名：<input type="text" name="uname" /><br/>
        用户年龄：<input type="text" name="age" /><br/>
        用户生日：<input type="text" name="date" /><br/>
        <input type="submit" value="提交" />
    </form>

    <a href="param/testServlet">Servlet原生的API</a>
</body>
</html>
```

其中输入日期需要自定义格式转换

在cn.itxiaoliu下新建utils.StringToDateConverter.java

```java
public class StringToDateConverter implements Converter<String,Date>{
    public Date convert(String source) {
        // 判断
        if(source == null){
            throw new RuntimeException("请您传入数据");
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 把字符串转换日期
            return df.parse(source);
        } catch (Exception e) {
            throw new RuntimeException("数据类型转换出现错误");
        }
    }
}
```

springmvc.xml中加入以下配置

```xml
    <!--配置自定义类型转换器-->
    <bean id="conversionService" class="org.springframework.context.support.ConversionServiceFactoryBean">
        <property name="converters">
            <set>
                <bean class="cn.itcast.utils.StringToDateConverter"/>
            </set>
        </property>
    </bean>
```

并在web.xml中加入以下配置解决表单输入中的中文乱码问题

```xml
  <!--配置解决中文乱码的过滤器-->
  <filter>
    <filter-name>characterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>characterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

##         **常用注解**  

在controller包下新建AnnoController.java

```java
/**
 * 常用的注解
 */
@Controller
@RequestMapping("/anno")
@SessionAttributes(value={"msg"})   // 把msg=美美存入到session域对中
public class AnnoController {
    @RequestMapping("/testRequestParam")
    public String testRequestParam(@RequestParam(name="name") String username){
        System.out.println("执行了...");
        System.out.println(username);
        return "success";
    }

    /**
     * 获取到请求体的内容
     * @return
     */
    @RequestMapping("/testRequestBody")
    public String testRequestBody(@RequestBody String body){
        System.out.println("执行了...");
        System.out.println(body);
        return "success";
    }

    /**
     * PathVariable注解
     * @return
     */
    @RequestMapping(value="/testPathVariable/{sid}")
    public String testPathVariable(@PathVariable(name="sid") String id){
        System.out.println("执行了...");
        System.out.println(id);
        return "success";
    }

    /**
     * 获取请求头的值
     * @param header
     * @return
     */
    @RequestMapping(value="/testRequestHeader")
    public String testRequestHeader(@RequestHeader(value="Accept") String header, HttpServletRequest request,HttpServletResponse response) throws IOException {
        System.out.println("执行了...");
        System.out.println(header);
        // return "success";
        // response.sendRedirect(request.getContextPath()+"/anno/testCookieValue");
        return "redirect:/param.jsp";
    }

    /**
     * 获取Cookie的值
     * @return
     */
    @RequestMapping(value="/testCookieValue")
    public String testCookieValue(@CookieValue(value="JSESSIONID") String cookieValue){
        System.out.println("执行了...");
        System.out.println(cookieValue);
        return "success";
    }

    /**
     * ModelAttribute注解
     * @return
     */
    @RequestMapping(value="/testModelAttribute")
    public String testModelAttribute(@ModelAttribute("abc") User user){
        System.out.println("testModelAttribute执行了...");
        System.out.println(user);
        return "success";
    }

    @ModelAttribute
    public void showUser(String uname, Map<String,User> map){
        System.out.println("showUser执行了...");
        // 通过用户查询数据库（模拟）
        User user = new User();
        user.setUname(uname);
        user.setAge(20);
        user.setDate(new Date());
        map.put("abc",user);
    }
//     * 该方法会先执行

//    @ModelAttribute
//    public User showUser(String uname){
//        System.out.println("showUser执行了...");
//        // 通过用户查询数据库（模拟）
//        User user = new User();
//        user.setUname(uname);
//        user.setAge(20);
//        user.setDate(new Date());
//        return user;
//    }


    /**
     * SessionAttributes的注解
     * @return
     */
    @RequestMapping(value="/testSessionAttributes")
    public String testSessionAttributes(Model model){
        System.out.println("testSessionAttributes...");
        // 底层会存储到request域对象中
        model.addAttribute("msg","美美");
        return "success";
    }

    /**
     * 获取值
     * @param modelMap
     * @return
     */
    @RequestMapping(value="/getSessionAttributes")
    public String getSessionAttributes(ModelMap modelMap){
        System.out.println("getSessionAttributes...");
        String msg = (String) modelMap.get("msg");
        System.out.println(msg);
        return "success";
    }

    /**
     * 清除
     * @param status
     * @return
     */
    @RequestMapping(value="/delSessionAttributes")
    public String delSessionAttributes(SessionStatus status){
        System.out.println("getSessionAttributes...");
        status.setComplete();
        return "success";
    }
}
```

webapp下新建anno.jsp

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%--常用的注解--%>
    <a href="anno/testRequestParam?name=哈哈">RequestParam</a>
    <br>
    <form action="anno/testRequestBody" method="post">
        用户姓名：<input type="text" name="username" /><br/>
        用户年龄：<input type="text" name="age" /><br/>
        <input type="submit" value="提交" />
    </form>

    <a href="anno/testPathVariable/10">testPathVariable</a>
    <br>
    <a href="anno/testRequestHeader">RequestHeader</a>
    <br>
    <a href="anno/testCookieValue">CookieValue</a>
    <br>
    <form action="anno/testModelAttribute" method="post">
        用户姓名：<input type="text" name="uname" /><br/>
        用户年龄：<input type="text" name="age" /><br/>
        <input type="submit" value="提交" />
    </form>

    <br>

    <a href="anno/testSessionAttributes">testSessionAttributes</a>
    <a href="anno/getSessionAttributes">getSessionAttributes</a>
    <a href="anno/delSessionAttributes">delSessionAttributes</a>
</body>
</html>
```

文件目录如下

![1591279726090](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591279726090.png)