package com.rose.controllers;

import com.rose.entities.Category;
import com.rose.repositories.CategoryRepository;
import com.rose.repositories.ProductSearchRepository;
import com.rose.security.CustomUserDetailsService;
import com.rose.security.TokenProvider;
import com.rose.security.oauth2.CustomOAuth2UserService;
import com.rose.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.rose.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.rose.services.impl.CategoryServiceImpl;
import org.elasticsearch.threadpool.ThreadPool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.CoreMatchers.is;

@WebMvcTest(value =CategoryController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@RunWith(SpringRunner.class)
public class CategoryControllerTest {
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @MockBean
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @MockBean
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private CategoryServiceImpl categoryService;

    @MockBean
    private ProductSearchRepository productSearchRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenListOfCategories_whenGetAllCategories_thenReturnCategoryList() throws Exception {
        List<Category> categoryList = Arrays.asList(
                new Category("AK733", "Category 1", true),
                new Category("AK732", "Category 2", true),
                new Category("AK734", "Category 3", false));
        Mockito.when(categoryService.findAll()).thenReturn(categoryList);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/collections"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is("Query successfully...")))
                .andExpect(jsonPath("$.data.*", hasSize(2)))
                .andExpect(jsonPath("$.data[1]", is(categoryList.get(1).getCategoryName())))

        ;
        Mockito.verify(categoryService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    @Test
    public void givenListOfCategories_whenGetAllCategories_thenReturnCategoryList2() throws Exception {
        List<Category> categoryList = Arrays.asList(
                new Category("AK733", "Category 1", true),
                new Category("AK732", "Category 2", true),
                new Category("AK734", "Category 3", false));
        Mockito.when(categoryService.findAll()).thenReturn(categoryList);

        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/collections"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("OK")))
                .andExpect(jsonPath("$.message", is("Query successfully...")))
                .andExpect(jsonPath("$.data.*", hasSize(2)))
                .andExpect(jsonPath("$.data[1]", is(categoryList.get(1).getCategoryName())))
                .andExpect(jsonPath("$.data[0]", is(categoryList.get(0).getCategoryName())))

        ;
        Mockito.verify(categoryService, Mockito.times(1)).findAll();
        Mockito.verifyNoMoreInteractions(categoryService);
    }

//    @Test
//    public void givenListOfCategories_whenGetAllCategories_thenReturnCategoryList3() throws Exception {
//        List<Category> categoryList = Arrays.asList(
//                new Category("AK733", "Category 1", true),
//                new Category("AK732", "Category 2", true),
//                new Category("AK734", "Category 3", false));
//        Mockito.when(categoryService.findAll()).thenReturn(categoryList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/collections/category"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status", is("OK")))
//                .andExpect(jsonPath("$.message", is("Query successfully...")))
//                .andExpect(jsonPath("$.data.*", hasSize(2)))
//                .andExpect(jsonPath("$.data[0]", is(categoryList.get(0).getCategoryName())))
//        ;
//        Mockito.verify(categoryService, Mockito.times(1)).findAll();
//        Mockito.verifyNoMoreInteractions(categoryService);
//    }
//
//    @Test
//    public void givenListOfCategories_whenGetAllCategories_thenReturnCategoryList4() throws Exception {
//        List<Category> categoryList = Arrays.asList(
//                new Category("AK733", "Category 1", true),
//                new Category("AK732", "Category 2", true),
//                new Category("AK734", "Category 3", false));
//        Mockito.when(categoryService.findAll()).thenReturn(categoryList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/management/collections"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status", is("OK")))
//                .andExpect(jsonPath("$.message", is("Query successfully...")))
//                .andExpect(jsonPath("$.data.*", hasSize(2)))
//                .andExpect(jsonPath("$.data.*", is(categoryList)))
//        ;
//        Mockito.verify(categoryService, Mockito.times(1)).findAll();
//        Mockito.verifyNoMoreInteractions(categoryService);
//    }
//
//    @Test
//    public void createCategory() throws Exception {
////        List<Category> categoryList = Arrays.asList(
////                new Category("AK733", "Category 1", true),
////                new Category("AK732", "Category 2", true),
////                new Category("AK734", "Category 3", false));
////        Mockito.when(categoryService.findAll()).thenReturn(categoryList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/management/collections"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
////                .andExpect(jsonPath("$.status", is("CREATED")))
////                .andExpect(jsonPath("$.message", is("Query successfully...")))
//                .andExpect(jsonPath("$.data.*", hasSize(2)))
//                .andExpect(jsonPath("$.data.*", is(categoryService)))
//                .andExpect(status().isCreated())
//        ;
//        Mockito.verify(categoryService, Mockito.times(1)).findAll();
//        Mockito.verifyNoMoreInteractions(categoryService);
//    }


}
