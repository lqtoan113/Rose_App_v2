package com.rose.controllers;

import com.rose.entities.Account;
import com.rose.entities.Role;
import com.rose.entities.enums.EAuthProvider;
import com.rose.entities.enums.EOrder;
import com.rose.entities.enums.ERole;
import com.rose.exceptions.CustomException;
import com.rose.models.LoginForm;
import com.rose.models.MailObject;
import com.rose.models.ResponseObject;
import com.rose.models.SmsObject;
import com.rose.models.account.*;
import com.rose.models.recaptcha.RecaptchaResponse;
import com.rose.security.TokenProvider;
import com.rose.services.impl.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v2/")
public class AuthController {
    @Value("${my.app.twilio.defaultphone}")
    private String defaultNumberPhone;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AccountServiceImpl accountService;
    @Autowired
    private PaymentServiceImpl paymentService;
    @Autowired
    private MailServiceImpl mailService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private RoleServiceImpl roleService;
    @Autowired
    private CloudServiceImpl cloudService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OrderServiceImpl orderService;
    @Autowired
    private SmsServiceImpl smsService;
    @Autowired
    private RecaptchaServiceImpl recaptchaService;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/auth/refresh-token")
    public ResponseEntity<ResponseObject> doRefreshToken(@RequestParam String refreshToken) {
        Account user = accountService.findByUsernameOrEmail(tokenProvider.getUsernameFromToken(refreshToken)).orElseThrow(
                () -> new CustomException(HttpStatus.NOT_FOUND, "Account in refresh token is not found...")
        );
        boolean isTheSameRefreshToken = user.getRefreshToken().equals(refreshToken);
        if (tokenProvider.validateToken(refreshToken) && isTheSameRefreshToken && user.getActive()) {
            String accessToken = tokenProvider.generateJwtTokenFromUsername(user.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Refresh token successfully!", accessToken, 1)
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                new ResponseObject("NOT_ACCEPTABLE", "Please sign in to use resource", null, 0)
        );
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ResponseObject> registerUser(@Valid @RequestBody RegisterForm signUpRequest, @RequestParam(name = "g-recaptcha-response") String response) {
        RecaptchaResponse recaptchaResponse = recaptchaService.verify(response);

        if (recaptchaResponse.isSuccess()) {
            if (accountService.existsByUsername(signUpRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("BAD_REQUEST", "Username is already taken!", null, 0)
                );
            }

            if (accountService.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("BAD_REQUEST", "Email is already taken!", null, 0)
                );
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("CREATED", "User registered successfully!",
                            accountService.createDefaultAccount(signUpRequest), 1)
            );
        }

        return ResponseEntity.status(HttpStatus.LOCKED).body(
                new ResponseObject("LOCKED", "Request has been blocked!", recaptchaResponse.getScore(), null)
        );
    }

    @PostMapping("/auth/sign-in")
    public ResponseEntity<ResponseObject> doSignIn(@Valid @RequestBody LoginForm loginRequest, @RequestParam(name = "g-recaptcha-response") String response) {

        RecaptchaResponse recaptchaResponse = recaptchaService.verify(response);
        if (recaptchaResponse.isSuccess()) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                if (accountService.isBanned(authentication.getName())) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                            new ResponseObject("NOT_ACCEPTABLE", "Your account has been banned", null, 0)
                    );
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "Sign In successfully!", accountService.createJwtResponse(authentication), 0)
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "Wrong Username or Password", null, null)
            );
        }
        return ResponseEntity.status(HttpStatus.LOCKED).body(
                new ResponseObject("LOCKED", "Request has been blocked!", recaptchaResponse.getScore(), null)
        );
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<ResponseObject> doForgotPassword(@RequestParam String keyword) {
        Account account = accountService.findByUsernameOrEmail(keyword).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Your account not found!"));
        if (!account.getProvider().equals(EAuthProvider.local)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Your account is managed by a 3rd party!", null, null)
            );
        }
        try {
            MailObject mailObject = new MailObject(account.getEmail(), account.getFullName(), null, null,
                    "[Rose] Please reset your password",
                    tokenProvider.generateJwtTokenFromUsername(account.getUsername()), null);

            mailService.sendEmailForgotPassword(mailObject);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Successfully, please check your email", null, null)
            );
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<ResponseObject> doResetPassword(@RequestParam String token, @RequestParam String newPassword) {
        if (tokenProvider.validateToken(token) && token != null) {
            Account account = accountService.findByUsernameOrEmail(tokenProvider.getUsernameFromToken(token))
                    .orElseThrow(
                            () -> new CustomException(HttpStatus.BAD_REQUEST, "Your account is not found!")
                    );
            account.setPassword(encoder.encode(newPassword));
            accountService.updateAccount(account);
            mailService.sendEmailForgotPasswordSuccess(
                    new MailObject(account.getEmail(), account.getFullName(), null, null,
                            "[GitHub] Your password was reset", null, null));

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("CREATED", "Reset successfully... Please login!", null, null)
            );
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseObject("CONFLICT", "Reset failed... Please try again!", null, null)
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> getProfile(Authentication authentication) {
        Account account = accountService.findByUsernameOrEmail(authentication.getName()).get();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Account, AccountDto>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
            }
        });

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully",
                        modelMapper.map(account,
                                AccountDto.class), 1)
        );
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> updateProfile(Authentication authentication, @RequestBody @Valid ProfileRequest profileRequest) {
        Account account = accountService.findByUsernameOrEmail(authentication.getName()).get();
        if (!account.getEmail().equalsIgnoreCase(profileRequest.getEmail())) {
            if (accountService.existsByEmail(profileRequest.getEmail())) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "Email already taken...");
            }
        }
        modelMapper.map(profileRequest, account);
        accountService.updateAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update profile successfully", null, 1)
        );
    }

    @PostMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<ResponseObject> updateProfileImage(Authentication authentication, @RequestParam MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Please select a file...", null, null)
                );
            } else if (file.getSize() > 1048576L) {// 5MB
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                        new ResponseObject(HttpStatus.NOT_ACCEPTABLE.toString(), "File too large...", null, null)
                );
            }
            Account account = accountService.findByUsernameOrEmail(authentication.getName()).get();
            account.setPhoto(cloudService.getFileUrl(cloudService.saveMultipartFile(file)));

            accountService.updateAccount(account);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject(HttpStatus.CREATED.toString(), "Upload file successfully!", account.getPhoto(), null)
            );
        } catch (Exception e) {
            throw new CustomException(HttpStatus.CONFLICT, " Something went wrong....");
        }
    }

    @PostMapping("/me/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> changePassword(Authentication auth, @RequestParam String oldPassword, @RequestParam String newPassword) {
        Account account = accountService.findByUsernameOrEmail(auth.getName()).get();
        boolean isCorrect = encoder.matches(oldPassword, account.getPassword());
        if (isCorrect) {
            account.setPassword(encoder.encode(newPassword));
            smsService.sendSms(new SmsObject(defaultNumberPhone,
                    "Your password has been changed, Please contact to rose.app.service@gmail.com or hotline:0961008102"));
            accountService.updateAccount(account);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Change password successfully!", null, 1)
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                new ResponseObject("NOT_ACCEPTABLE", "Old password incorrect!", null, 0)
        );
    }

    @GetMapping("/me/recharge")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> doRecharge(Authentication auth, HttpServletRequest request, @RequestParam Double amount) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK VNPay", "Create order successfully...", orderService.createOrderRecharge(auth, request, amount), 1)
        );

    }

    @GetMapping("/me/history-payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseObject> getMyHistoryPayment(Authentication auth) {
        List<HistoryPaymentResponse> payments = paymentService.getHistoryPaymentByUsername(auth.getName())
                .stream().map(HistoryPaymentResponse::new)
                .collect(Collectors.toList());
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "The account has not had any transactions.", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", payments, payments.size())
        );
    }

    @GetMapping("/management/accounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAllAccounts() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Account, AccountDto>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
            }
        });
        List<Account> accountList = accountService.findAll();

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject(HttpStatus.OK.toString(), "Query file successfully!",
                        modelMapper.map(accountList,
                                new TypeToken<List<AccountDto>>() {
                                }.getType()), null)
        );
    }

    @GetMapping("/management/accounts/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getAccount(@PathVariable String username) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<Account, AccountDto>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
            }
        });
        Account account = accountService.findByUsernameOrEmail(username).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "Account is not found!")
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query file successfully!", account, null)
        );
    }

    @GetMapping("/management/accounts/{username}/history-payment")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> getHistoryPaymentByUsername(@PathVariable String username) {
        List<HistoryPaymentResponse> payments = paymentService.getHistoryPaymentByUsername(username)
                .stream().map(HistoryPaymentResponse::new)
                .collect(Collectors.toList());
        if (payments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "The account has not had any transactions.", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Query successfully...", payments, payments.size())
        );
    }

    @PostMapping("/management/accounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> createNewAccount(Authentication auth, @RequestBody @Valid AccountRequest accountRequest) {
        if (accountService.existsByUsername(accountRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Username is already taken!", null, 0)
            );
        }
        if (accountService.existsByEmail(accountRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("BAD_REQUEST", "Email is already taken!", null, 0)
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("CREATED", "User registered successfully!",
                        accountService.createAccount(auth, accountRequest), 1)
        );
    }

    @PutMapping("/management/accounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> updateAccount(Authentication auth, @RequestBody @Valid AccountRequest accountRequest) {

        if (auth.getName().equals(accountRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't edit yourself...", null, null)
            );
        }

        Account account = accountService.findByUsernameOrEmail(accountRequest.getUsername()).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Your account is not found!")
                );

        if (auth.getAuthorities().stream().noneMatch(i -> i.getAuthority().equals(ERole.ROLE_ADMIN.toString())) &&
                account.getRoles().stream().anyMatch(i -> i.getName().equals(ERole.ROLE_ADMIN))) {

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't edit your manager...", null, null)
            );
        }

        Set<Role> roleSet = roleService.getRolesBySetRoles(accountRequest.getRoles());
        ModelMapper mapper = new ModelMapper();

        if (auth.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(ERole.ROLE_ADMIN.toString()))) {
            roleSet.removeIf(role -> role.getName().equals(ERole.ROLE_ADMIN));
        }

        mapper.addMappings(new PropertyMap<AccountRequest, Account>() {
            @Override
            protected void configure() {
                skip(destination.getPassword());
                skip(destination.getUsername());
                skip(destination.getCreateDate());
            }
        });
        mapper.map(accountRequest, account);
        account.setRoles(roleSet);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update successfully!", accountService.updateAccount(account), null)
        );
    }

    @DeleteMapping("/management/accounts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ResponseObject> doDeleteAccount(Authentication auth, @RequestParam String username, @RequestParam String description) {
        if (auth.getName().equals(username)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't edit yourself...", null, null)
            );
        }
        Account account = accountService.findByUsernameOrEmail(username).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Your account is not found!")
                );

        if (auth.getAuthorities().stream().noneMatch(i -> i.getAuthority().equals(ERole.ROLE_ADMIN.toString())) &&
                account.getRoles().stream().anyMatch(i -> i.getName().equals(ERole.ROLE_ADMIN))) {

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(
                    new ResponseObject("NOT_ACCEPTABLE", "You can't edit your manager...", null, null)
            );
        }
        account.setActive(!account.getActive());
        accountService.updateAccount(account);

        if (account.getActive()) {
            smsService.sendSms(new SmsObject(defaultNumberPhone,
                    "Your account has been unlock, You can continue use our application!"));
        } else {
            account.getOrderList().stream()
                    .filter(order -> order.getStatus().equals(EOrder.PENDING_ACCEPT)
                            || order.getStatus().equals(EOrder.ACCEPTED))
                    .peek(o -> {
                        o.setStatus(EOrder.CANCELLED);
                        orderService.rollbackOrder(o);
                        orderService.updateOrder(o);
                    }).close();
            smsService.sendSms(new SmsObject(defaultNumberPhone,
                    "Your account has been banned cause " + description + ", Please contact to rose.app.service@gmail.com or hotline:0961008102!"));
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Handle successfully!", null, null)
        );
    }
}
