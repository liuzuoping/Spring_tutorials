package jdbcTemplate;

import domain.Account;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class Demo1 {
    public static void main(String [] args){
        ApplicationContext ac = new ClassPathXmlApplicationContext("bean.xml");
        JdbcTemplate jt=ac.getBean("jdbcTemplate",JdbcTemplate.class);
//        jt.execute("insert into account(name,money)values ('nnn',333)");
//        jt.update("insert into account(name,money)values(?,?)","ggggg",45621f );
//        jt.update("update account set money=?,name=? where id=?",456786,"test6",9 );
//        jt.update("delete from account where id=?",9 );
//        List<Account> accounts=jt.query("select * from account where money>?",new BeanPropertyRowMapper<Account>(Account.class),10000f );
//        for (Account account : accounts) {
//            System.out.println(account);
//        }

        List<Account> accounts=jt.query("select * from account where id=?",new BeanPropertyRowMapper<Account>(Account.class),1 );
        System.out.println(accounts.isEmpty()?"没有内容":accounts.get(0));

        Long count=jt.queryForObject("select count(*) from account where money>?",Long.class,1000f );
        System.out.println(count);
    }
}
