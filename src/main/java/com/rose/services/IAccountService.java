package com.rose.services;

import com.rose.entities.Account;
import com.rose.models.JwtResponse;
import com.rose.models.account.RegisterForm;
import com.rose.models.account.AccountRequest;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IAccountService {
    Account createDefaultAccount(RegisterForm form);

    JwtResponse createJwtResponse(Authentication authentication);

    Optional<Account> findByUsernameOrEmail(String keywords);

    boolean existsByUsername(String username);

    Optional<Account> findByUsername(String username);

    boolean existsByEmail(String email);

    List<Account> findAll();
    Account updateAccount(Account account);

    void saveRefreshToken(String username, String refreshToken);

    Boolean isBanned(String username);

    Account createAccount(Authentication authentication,AccountRequest accountRequest);

    Optional<Account> findByRefreshToken(String refreshToken);

    Boolean isEmptyNumberPhone(Authentication authentication);

    List<Object[]> top10Customer();

    Long getToTalAccount();

    Long getToTalAccountActive();

    Long getTotalMaleUser();

    Long getTotalFemaleUser();
}
