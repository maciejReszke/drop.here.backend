package com.drop.here.backend.drophere.authentication.account.service;

import com.drop.here.backend.drophere.authentication.account.entity.Account;
import com.drop.here.backend.drophere.authentication.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountPersistenceService {
    private final AccountRepository accountRepository;

    public Mono<Account> createAccount(Account account) {
        log.info("Saving new account with type {}", account.getAccountType());
        return accountRepository.save(account);
    }

    public Mono<Account> updateAccount(Account account) {
        log.info("Updating account with id {} and type {}", account.getId(), account.getAccountType());
        return accountRepository.save(account);
    }

    public Mono<Account> findByMail(String mail) {
        return accountRepository.findByMail(mail);
    }
}
