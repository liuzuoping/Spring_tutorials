## Spring中的JdbcTemplate 的增删改查操作  

案例的前期准备，数据库中建立account表

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
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.2.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.2.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.44</version>
        </dependency>
    </dependencies>
```

###         文件结构如下

![1591180766653](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591180766653.png)

### **在** **spring** **配置文件中配置** **JdbcTemplate**

```java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>
        
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
        <property name="url" value="jdbc:mysql:///address"></property>
        <property name="username" value="xiaoliu"></property>
        <property name="password" value="960614abcd"></property>
    </bean>
</beans>

```

### java目录下新建domain.Account.java

```java
public class Account implements Serializable {
    private Integer id;
    private String name;
    private Float money;
//setters+getters+toStrings()
```

#### java目录下新建jdbcTemplate.Demo.java

```java
public class Demo {
    public static void main(String [] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jt=ac.getBean("jdbcTemplate",JdbcTemplate.class);
        //插入操作
        jt.execute("insert into account(name,money)values ('nnn',333)");
        jt.update("insert into account(name,money)values(?,?)","ggggg",45621f );
        //更新操作
        jt.update("update account set money=?,name=? where id=?",456786,"test6",9 );
        //删除操作
        jt.update("delete from account where id=?",9 );
        //查找所有操作
        List<Account> accounts=jt.query("select * from account where money>?",new BeanPropertyRowMapper<Account>(Account.class),10000f );
        for (Account account : accounts) {
            System.out.println(account);
        }
		//查询一个操作
        List<Account> accounts=jt.query("select * from account where id=?",new BeanPropertyRowMapper<Account>(Account.class),1 );
        System.out.println(accounts.isEmpty()?"没有内容":accounts.get(0));

        //查询个数
        Long count=jt.queryForObject("select count(*) from account where money>?",Long.class,1000f );
        System.out.println(count);
    }
}
```

## dao 中使用 JdbcTemplate

持久层接口

```java
public interface IAccountDao {
    Account findAccountById(Integer accountId);
    Account findAccountByname(String accountName);
    void updateAccount(Account account);
}
```

持久层实现类

```java
public class AccountDaoImpl implements IAccountDao {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Account findAccountById(Integer accountId) {
        List<Account> accounts= jdbcTemplate.query("select * from account where id=?", new BeanPropertyRowMapper<Account>(Account.class),accountId);
        return accounts.isEmpty()?null:accounts.get(0);
    }

    public Account findAccountByname(String accountName) {
        List<Account> accounts= jdbcTemplate.query("select * from account where name=?", new BeanPropertyRowMapper<Account>(Account.class),accountName);
        if (accounts.isEmpty()){
            return null;
        }
        if(accounts.size()>1){
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    public void updateAccount(Account account) {
        jdbcTemplate.update("update account set name=?,money=? where id=?",account.getName(),account.getMoney(),account.getId());
    }
}
```

配置文件中加入以下配置

```xml
    <bean id="accountDao" class="dao.impl.AccountDaoImpl">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
        <property name="dataSource" ref="dataSource"></property>
    </bean>
```

### **第二种方式：让** **dao** **继承** **JdbcDaoSupport**

```java
public class JdbcDaoSupport {
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void setDataSource(DataSource dataSource) {
        if(jdbcTemplate==null){
            jdbcTemplate=createJdbcTemplate(dataSource);
        }
    }
    private  JdbcTemplate createJdbcTemplate(DataSource dataSource){
        return  new JdbcTemplate(dataSource);
    }
}
```

业务层实现类如下

```java
public class AccountDaoImpl  extends  JdbcDaoSupport implements IAccountDao {

    public Account findAccountById(Integer accountId) {
        List<Account> accounts= super.getJdbcTemplate().query("select * from account where id=?", new BeanPropertyRowMapper<Account>(Account.class),accountId);
        return accounts.isEmpty()?null:accounts.get(0);
    }

    public Account findAccountByname(String accountName) {
        List<Account> accounts= super.getJdbcTemplate().query("select * from account where name=?", new BeanPropertyRowMapper<Account>(Account.class),accountName);
        if (accounts.isEmpty()){
            return null;
        }
        if(accounts.size()>1){
            throw new RuntimeException("结果集不唯一");
        }
        return accounts.get(0);
    }

    public void updateAccount(Account account) {
        super.getJdbcTemplate().update("update account set name=?,money=? where id=?",account.getName(),account.getMoney(),account.getId());
    }
}
```

jdbcTemplate包下新建测试代码Demo4.java

```java
public class Demo4 {
    public static void main(String [] args){
        ApplicationContext ac=new ClassPathXmlApplicationContext("bean.xml");
        IAccountDao accountDao=ac.getBean("accountDao",IAccountDao.class);
        Account account=accountDao.findAccountById(1);
        System.out.println(account);
        account.setMoney(30000f);
        accountDao.updateAccount(account);
    }
}
```

