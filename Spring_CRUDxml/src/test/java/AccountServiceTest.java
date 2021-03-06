import domain.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import service.IAccountService;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:bean.xml")
public class AccountServiceTest {
    @Autowired
    private IAccountService as;
    @Test
    public void testFindAll(){
        List<Account> accounts=as.findAllAccount();
        for (Account account : accounts) {
            System.out.println(account);
        }
    }
    @Test
    public void testFindOne(){
        Account account=as.findAccountById(1);
        System.out.println(account);
    }
    @Test
    public void testSave(){
        Account account=new Account();
        account.setName("test3");
        account.setMoney(456789f);
        as.saveAccount(account);
    }
    @Test
    public void testUpdate(){
        Account account=as.findAccountById(7);
        account.setMoney(78951f);
        as.updateAccount(account);
    }
    @Test
    public void testDelete(){
        as.deleteAccount(12);
    }
}
