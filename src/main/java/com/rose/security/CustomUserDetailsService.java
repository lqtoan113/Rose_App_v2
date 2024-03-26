package com.rose.security;



import com.rose.entities.Account;
import com.rose.repositories.AccountRepository;
import com.rose.services.impl.AccountServiceImpl;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    AccountServiceImpl accountService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Account user = accountService.findByUsernameOrEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email : " + email)
        );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(String username) {
        Account user = accountService.findByUsernameOrEmail(username).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", username)
        );

        return UserPrincipal.create(user);
    }
}