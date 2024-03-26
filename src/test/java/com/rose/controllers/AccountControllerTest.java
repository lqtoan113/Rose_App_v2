//package com.rose.controllers;
//
//import com.rose.entities.Account;
//import com.rose.repositories.ProductSearchRepository;
//import com.rose.security.CustomUserDetailsService;
//import com.rose.security.TokenProvider;
//import com.rose.security.oauth2.CustomOAuth2UserService;
//import com.rose.security.oauth2.OAuth2AuthenticationFailureHandler;
//import com.rose.security.oauth2.OAuth2AuthenticationSuccessHandler;
//import com.rose.services.impl.*;
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import java.util.Optional;
//
//@WebMvcTest(value = AuthController.class,  excludeAutoConfiguration = {SecurityAutoConfiguration.class} )
//@AutoConfigureMockMvc(addFilters = false)
//@RunWith(SpringRunner.class)
//public class AccountControllerTest {
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private AccountServiceImpl accountService;
//
//    @MockBean
//    private ProductServiceImpl productService;
//
//    @MockBean
//    private ProductEntryServiceImpl entryService;
//
//    @MockBean
//    private ProductSearchRepository productSearchRepository;
//
//    @MockBean
//    private PaymentServiceImpl paymentService;
//
//    @MockBean
//    private MailServiceImpl mailService;
//    @MockBean
//    private PasswordEncoder encoder;
//    @MockBean
//    private RoleServiceImpl roleService;
//    @MockBean
//    private CloudServiceImpl cloudService;
//    @MockBean
//    private ModelMapper modelMapper;
//    @MockBean
//    private OrderServiceImpl orderService;
//    @MockBean
//    private SmsServiceImpl smsService;
//    @MockBean
//    private RecaptchaServiceImpl recaptchaService;
//
//    @MockBean
//    private CustomUserDetailsService customUserDetailsService;
//
//    @MockBean
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    @MockBean
//    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
//
//    @MockBean
//    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
//
//    @MockBean
//    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
//
//    @MockBean
//    private TokenProvider tokenProvider;
//    @Test
//    public void givenObjectAccount_whenfindById_thenReturnObjectAccount(){
//        Account user1 = new Account("bathimanh", "123456", null, "samakirito5@gmail.com", "0788937674", "Ba Thi Manh", null,
//                true, "TP HCM", true, 100.0, null, null, null, null, null
//                , null, null);
//
//        Account user2 = new Account("hieunguyen", "28092002", null, "hieuheo@gmail.com", "0961008102", "Nguyen Chi Hieu", null,
//                true, "Ben Tre", true, 900.0, null, null, null, null, null
//                , null, null);
//
//        Account user3 = new Account("Thaoheo", null, null, "thaoheo27@gmail.com", null, null, null,
//                false, null, false, null, null, null, null, null, null
//                , null, null);
//
//        Mockito.when(accountService.findByUsernameOrEmail(user1.getUsername())).thenReturn(Optional.of(user1));
//        Mockito.when(accountService.findByUsernameOrEmail(user1.getEmail())).thenReturn(Optional.of(user2));
//
//        //check trường hợp tìm được username: status passed
//        Assertions.assertThat(accountService.findByUsernameOrEmail("bathimanh")).isEqualTo(Optional.of(user1));
//
//        //check trường hợp không tìm được username: status failed
//        Assertions.assertThat(accountService.findByUsernameOrEmail("haha")).isEqualTo(Optional.of(user1));
//
//        //check trường hợp tìm được email: status passed
//        Assertions.assertThat(accountService.findByUsernameOrEmail("lekiet1127@gmail.com")).isEqualTo(Optional.of(user1));
//
//        //check trường hợp không tìm được email: status failed
//        Assertions.assertThat(accountService.findByUsernameOrEmail("lekiet1128@gmail.com")).isEqualTo(Optional.of(user1));
//
//        //check hợp so sánh list sau khi tìm được user
//        Assertions.assertThat(accountService.findByUsernameOrEmail("bathimanh")).isEqualTo(Optional.of(user3));
//
//
//    }
//}
