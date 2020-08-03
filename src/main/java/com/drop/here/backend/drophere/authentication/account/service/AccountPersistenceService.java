package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountPersistenceService {
    private final AccountRepository accountRepository;

    public void createAccount(Account account) {
        log.info("Saving account with mail {} and type {}", account.getMail(), account.getAccountType());
        accountRepository.save(account);
        log.info("Successfully saved account with mail {} and type {}", account.getMail(), account.getAccountType());
    }

    public Optional<Account> findByMail(String mail) {
        return accountRepository.findByMail(mail);
    }
}
