package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AccountPersistenceService {
    private final AccountRepository accountRepository;

    public void createAccount(Account account) {
        log.info("Saving new account with type {}", account.getAccountType());
        accountRepository.save(account);
    }

    public void updateAccount(Account account) {
        log.info("Updating account with id {} and type {}", account.getId(), account.getAccountType());
        accountRepository.save(account);
    }

    public Optional<Account> findByMail(String mail) {
        return accountRepository.findByMail(mail);
    }

    public Optional<Account> findByMailWithRoles(String mail) {
        return accountRepository.findByMailWithRoles(mail);
    }
}
