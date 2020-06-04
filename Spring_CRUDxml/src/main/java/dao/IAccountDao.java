package dao;

import domain.Account;

import java.util.List;

public interface IAccountDao {
    List<Account> findAllAcount();
    Account findAccountById(Integer accountId);
    void saveAccount(Account account);
    void updateAccount(Account account);
    void deleteAccount(Integer accountId);
}
