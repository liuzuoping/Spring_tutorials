package service.impl;

import dao.IAccountDao;
import dao.impl.AccountDaoImpl;
import service.IAccountService;

public class AccountServiceImpl implements IAccountService {
    private IAccountDao accountDao=new AccountDaoImpl();
    public AccountServiceImpl(){
        System.out.println("对象创建了");
    }

    public void setAccount(){
        accountDao.saveAccount();

    }

    public void saveAccount() {
         System.out.println("保存了账户");
    }
}
