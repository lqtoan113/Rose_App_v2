//package com.rose.controllers;
//
//import com.rose.entities.Account;
//import com.rose.entities.Payment;
//import com.rose.repositories.ProductSearchRepository;
//import com.rose.security.CustomUserDetailsService;
//import com.rose.security.TokenProvider;
//import com.rose.security.oauth2.CustomOAuth2UserService;
//import com.rose.security.oauth2.OAuth2AuthenticationFailureHandler;
//import com.rose.security.oauth2.OAuth2AuthenticationSuccessHandler;
//import com.rose.services.impl.*;
//import org.assertj.core.api.Assertions;
//import org.hamcrest.collection.IsEmptyCollection;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//
//@WebMvcTest(value = AuthController.class,  excludeAutoConfiguration = {SecurityAutoConfiguration.class} )
//@RunWith(SpringRunner.class)
//public class HistoryPaymentAccountTest {
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
//    private OrderServiceImpl orderService;
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
//
//    @MockBean
//    private PasswordEncoder encoder;
//
//    @MockBean
//    private RoleServiceImpl roleService;
//
//    @MockBean
//    private CloudServiceImpl cloudService;
//
//    @MockBean
//    private ModelMapper modelMapper;
//
//    @MockBean
//    private SmsServiceImpl smsService;
//
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
//
//
//    @Test
//    public void givenListPayment_whenfindByUsername_thenReturnListPayment() throws Exception{
//        Account user = new Account("bathimanh", "123456", null, "samakirito5@gmail.com", "0788937674", "Ba Thi Manh", null,
//                true, "TP HCM", true, 100.0, null, null, null, null, null
//                , null, null);
//
//       List<Payment> paymentUser = Arrays.asList( new Payment("001",null,null,null,
//               null,null,800.0,"bl01",null,"COD",
//               null,null,null,null,null,null,null));
//
//        List<Payment> paymentUser2 = Arrays.asList( new Payment(null,null,null,null,
//                null,null,1000.0,null,null,null,
//                null,null,null,null,null,null,null));
//
//        Mockito.when(paymentService.getHistoryPaymentByUsername(user.getUsername())).thenReturn(paymentUser);
//        System.out.println(user.getUsername());
//        System.out.println(paymentUser);
//
//        //check trường hợp trả lại history payment khi truyền username có trong list: status passed
//        Assertions.assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername())).isEqualTo(paymentUser);
//
//        //check trường hợp trả lại history payment khi truyền username không có trong list: status failed
//        Assertions.assertThat(paymentService.getHistoryPaymentByUsername("hehehe")).isEqualTo(paymentUser);
//
//        //check hợp so sánh list sau khi tìm được user, phải trả về list khác: status failed
//        Assertions.assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername())).isEqualTo(paymentUser2);
//
//        //check hợp trả về đúng số lương item trong list là 1 : status passed
//        assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername()), hasSize(1));
//
//        //check hợp trả về sai số lương item trong list : status failed
//        assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername()), hasSize(2));
//
//        //check trường hợp không trả về list null: status passed
//        assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername()), not( IsEmptyCollection.empty()));
//
//        //check trường hợp trả đúng thứ tự các field sau khi trả về list: status failed
//        assertThat(paymentService.getHistoryPaymentByUsername(user.getUsername()), contains(new Payment("COD",null,null,null,
//                   null,null,800.0,"bl001",null,"001",
//                   null,null,null,null,null,null,null)));
//    }
//}
