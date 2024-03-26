package com.rose.services.impl;

import com.rose.entities.Account;
import com.rose.entities.enums.EAuthProvider;
import com.rose.entities.enums.ERole;
import com.rose.entities.Role;
import com.rose.models.JwtResponse;
import com.rose.models.account.RegisterForm;
import com.rose.models.account.AccountRequest;
import com.rose.repositories.AccountRepository;
import com.rose.security.TokenProvider;
import com.rose.security.UserPrincipal;
import com.rose.services.IAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired private AccountRepository accountRepository;
    @Autowired private PasswordEncoder encoder;
    @Autowired private RoleServiceImpl roleService;
    @Autowired private TokenProvider tokenProvider;
    /**
     * @param form is {@link RegisterForm}
     * @return @{@link Account}
     */
    @Override
    public Account createDefaultAccount(RegisterForm form) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getDefaultRole());
        Account account = new Account(
                form.getFullName(),
                form.getUsername(),
                encoder.encode(form.getPassword()),
                form.getPhoneNumber(),
                form.getEmail(),
                roles,
                EAuthProvider.local);
        return accountRepository.save(account);
    }

    /**
     * @param authentication
     * @return
     */
    @Override
    @Transactional
    public JwtResponse createJwtResponse(Authentication authentication) {
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

        String refreshToken = tokenProvider.createToken(authentication, 96);
        accountRepository.saveRefreshToken(userDetails.getUsername(), refreshToken);

        return new JwtResponse(tokenProvider.createToken(authentication, 1), refreshToken);
    }

    /**
     * @param keywords
     * @return
     */
    @Override
    public Optional<Account> findByUsernameOrEmail(String keywords) {
       return accountRepository.findAccountByUsernameOrEmail(keywords);
    }

    /**
     * @param username
     * @return boolean
     */
    @Override
    public boolean existsByUsername(String username) {
        return accountRepository.existsByUsername(username);
    }

    /**
     * @param username
     * @return
     */
    @Override
    public Optional<Account> findByUsername(String username) {
       return accountRepository.findById(username);
    }

    /**
     * @param email
     * @return boolean
     */
    @Override
    public boolean existsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    /**
     * @return
     */
    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    /**
     * @param account
     */
    @Override
    public Account updateAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * @param username
     * @param refreshToken
     */
    @Override
    public void saveRefreshToken(String username, String refreshToken) {
        accountRepository.saveRefreshToken(username, refreshToken);
    }

    /**
     * @param username
     * @return
     */
    @Override
    public Boolean isBanned(String username) {
        return !accountRepository.findAccountByUsernameOrEmail(username).get().getActive();
    }

    /**
     * @param accountRequest
     * @return
     */
    @Override
    public Account createAccount(Authentication auth,AccountRequest accountRequest) {
        Account account = new Account(
                accountRequest.getUsername(),
                encoder.encode(accountRequest.getPassword()),
                accountRequest.getEmail(),
                accountRequest.getPhone(),
                accountRequest.getFullName(),
                accountRequest.getPhoto(),
                accountRequest.getGender(),
                accountRequest.getAddress(),
                accountRequest.getActive(),
                accountRequest.getBalance(),
                EAuthProvider.local
        );

        Set<Role> roleSet = roleService.getRolesBySetRoles(accountRequest.getRoles());

        if (auth.getAuthorities().stream().noneMatch( a -> a.getAuthority().equals(ERole.ROLE_ADMIN.toString()))){
            roleSet.removeIf(role -> role.getName().equals(ERole.ROLE_ADMIN));
        };
        account.setRoles(roleSet);
        return accountRepository.save(account);
    }

    /**
     * @param refreshToken
     * @return
     */
    @Override
    public Optional<Account> findByRefreshToken(String refreshToken) {
        return accountRepository.findAccountByRefreshToken(refreshToken);
    }

    /**
     * @param authentication
     * @return
     */
    @Override
    public Boolean isEmptyNumberPhone(Authentication authentication) {
        return StringUtils.isEmpty(accountRepository.findById(authentication.getName()).get().getPhone());
    }

    @Override
    public List<Object[]> top10Customer() {
        return accountRepository.top10Customer();
    }

    @Override
    public Long getToTalAccount() {
        return accountRepository.getToTalAccount();
    }

    @Override
    public Long getToTalAccountActive() {
        return accountRepository.getToTalAccountActive();
    }

    @Override
    public Long getTotalMaleUser() {
        return accountRepository.getTotalMaleUser() ;
    }

    @Override
    public Long getTotalFemaleUser() {
        return accountRepository.getTotalFemaleUser() ;
    }
}
