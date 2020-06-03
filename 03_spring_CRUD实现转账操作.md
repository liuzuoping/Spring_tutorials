## spring_CRUD实现转账操作

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
            <groupId>commons-dbutils</groupId>
            <artifactId>commons-dbutils</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.19</version>
        </dependency>
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.5</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.0.2.RELEASE</version>
        </dependency>
    </dependencies>
```

###         文件结构如下

![1591156981150](C:\Users\MI\AppData\Roaming\Typora\typora-user-images\1591156981150.png)

### 编写实体类  

```java
public class Account implements Serializable {
    private Integer id;
    private String name;
    private Float money;
//setters&getters+toStrings()
```

###         编写持久层代码  

​        账户的持久层接口  

```java
public interface IAccountDao {
    List<Account> findAllAccount();
    Account findAccountById(Integer accountId);
    void saveAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Integer accountId);
    Account findAccountByName(String accountName);
}
```

​        账户的持久层实现类  

```java
public class AccountDaoImpl implements IAccountDao {
    private Connectionutils connectionutils;

    public void setConnectionutils(Connectionutils connectionutils) {
        this.connectionutils = connectionutils;
    }

    private QueryRunner runner;

    public void setRunner(QueryRunner runner) {
        this.runner = runner;
    }

    public List<Account> findAllAccount() {
        try {
            return runner.query(connectionutils.getThreadConnection(),"select * from account", new BeanListHandler<Account>(Account.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Account findAccountById(Integer accountId) {
        try {
            return runner.query(connectionutils.getThreadConnection(),"select * from account where id=?", new BeanHandler<Account>(Account.class), accountId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveAccount(Account account) {
        try {
            runner.update(connectionutils.getThreadConnection(),"insert into account(name,money)values(?,?)",account.getName(),account.getMoney());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateAccount(Account account) {
        try {
            runner.update(connectionutils.getThreadConnection(),"update account set name=?,money=? where id=?",account.getName(),account.getMoney(),account.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAccount(Integer accountId) {
        try {
            runner.update(connectionutils.getThreadConnection(),"delete from account where id=?",accountId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Account findAccountByName(String accountName) {
        try {
            List<Account> accounts= runner.query(connectionutils.getThreadConnection(),"select * from account where name=?", new BeanListHandler<Account>(Account.class), accountName);
            if(accounts==null||accounts.size()==0){
                return null;
            }
            if(accounts.size()>1){
                throw new RuntimeException("结果集不唯一，数据有问题");
            }
            return accounts.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

###          编写业务层代码  

账户的业务层接口

```java
public interface IAccountService {
    List<Account> findAllAccount();
    Account findAccountById(Integer accountId);
    void saveAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Integer accountId);
    void transfer(String sourceName,String targetName,Float money);
}
```

​        账户的业务层实现类  

```java
public class AccountServiceImpl implements IAccountService {

    private IAccountDao accountDao;

    public void setAccountDao(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public List<Account> findAllAccount() {
        return accountDao.findAllAccount();
    }

    public Account findAccountById(Integer accountId) {
        return accountDao.findAccountById(accountId);
    }

    public void saveAccount(Account account) {
        accountDao.saveAccount(account);
    }

    public void updateAccount(Account account) {
        accountDao.updateAccount(account);
    }

    public void deleteAccount(Integer accountId) {
        accountDao.deleteAccount(accountId);
    }

    public void transfer(String sourceName, String targetName, Float money) {
        System.out.println("transfer.......");
        Account source=accountDao.findAccountByName(sourceName);
        Account target=accountDao.findAccountByName(targetName);
        source.setMoney(source.getMoney()-money);
        target.setMoney(target.getMoney()+money);
        accountDao.updateAccount(source);
        //int i=1/0;
        accountDao.updateAccount(target);
    }
}
```

### java目录下创建factory包并编写BeanFactory.java

```java
public class BeanFactory {
    private IAccountService accountService;
    private TransactionManager txManager;

    public void setTxManager(TransactionManager txManager) {
        this.txManager = txManager;
    }

    public final void setAccountService(IAccountService accountService) {
        this.accountService = accountService;
    }

    public IAccountService getAccountService() {
        Proxy.newProxyInstance(accountService.getClass().getClassLoader(),
                accountService.getClass().getInterfaces(),
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Object rtValue = null;
                        try {
                            txManager.beginTransaction();
                            rtValue = method.invoke(accountService, args);
                            txManager.commit();
                            return rtValue;
                        } catch (Exception e) {
                            txManager.rollback();
                            throw new RuntimeException(e);
                        } finally {
                            txManager.release();
                        }
                    }
                });
        return accountService;
    }
}
```



###          java目录下创建utils包并编写Connectionutils.java

```java
public class Connectionutils {
    private ThreadLocal<Connection> tl=new ThreadLocal<Connection>();
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getThreadConnection(){
        try {
            Connection conn=tl.get();
            if(conn==null){
                conn=dataSource.getConnection();
                tl.set(conn);
            }
            return conn;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public void removeConnection(){
        tl.remove();
    }
}
```

#### utils包下新建TransactionManager.java

```java
public class TransactionManager {
    private Connectionutils connectionutils;

    public void setConnectionutils(Connectionutils connectionutils) {
        this.connectionutils = connectionutils;
    }

    public void beginTransaction(){
        try {
            connectionutils.getThreadConnection().setAutoCommit(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void commit(){
        try {
            connectionutils.getThreadConnection().commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void rollback(){
        try {
            connectionutils.getThreadConnection().rollback();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void release(){
        try {
            connectionutils.getThreadConnection().close();
            connectionutils.removeConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
```

## 创建并编写配置文件  

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

        <bean id="proxyAccountService" factory-bean="beanFactory" factory-method="getAccountService"></bean>
        <bean id="beanFactory" class="factory.BeanFactory">
            <property name="accountService" ref="accountService"></property>
            <property name="txManager" ref="txManager"></property>
        </bean>
        <bean id="accountService" class="service.impl.AccountServiceImpl">
            <property name="accountDao" ref="accountDao"></property>
        </bean>
        <bean id="accountDao" class="dao.impl.AccountDaoImpl">
            <property name="runner" ref="runner"></property>
            <property name="connectionutils" ref="connectionutils"></property>
        </bean>
        <bean id="runner" class="org.apache.commons.dbutils.QueryRunner" scope="prototype"></bean>

        <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
            <property name="driverClass" value="com.mysql.jdbc.Driver"></property>
            <property name="jdbcUrl" value="jdbc:mysql:///address?serverTimezone=UTC"></property>
            <property name="user" value="xiaoliu"></property>
            <property name="password" value="960614abcd"></property>
        </bean>

        <bean id="connectionutils" class="utils.Connectionutils">
            <property name="dataSource" ref="dataSource"></property>
        </bean>

        <bean id="txManager" class="utils.TransactionManager">
            <property name="connectionutils" ref="connectionutils"></property>
        </bean>
</beans>
```

**测试类代码**

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceTest {
    @Autowired
    @Qualifier("proxyAccountService")
    private IAccountService as;
    @Test
    public void testTransfer(){
        as.transfer("aaa", "bbb", 1000f);
    }
}
```

