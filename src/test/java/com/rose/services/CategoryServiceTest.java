package com.rose.services;

import com.rose.entities.Category;
import com.rose.entities.Product;
import com.rose.repositories.CategoryRepository;
import com.rose.repositories.ProductSearchRepository;
import com.rose.security.CustomUserDetailsService;
import com.rose.security.TokenProvider;
import com.rose.security.oauth2.CustomOAuth2UserService;
import com.rose.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.rose.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.rose.services.impl.CategoryServiceImpl;
import org.elasticsearch.common.recycler.Recycler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.JsonPathAssertions;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;

@WebMvcTest(value = CategoryServiceImpl.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@RunWith(SpringRunner.class)
public class CategoryServiceTest {

    @MockBean
    private Category category;

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

    @MockBean
    private Product product;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenGetAllCategory_shouldReturnAllListCategories(){
        //given
        List<Category> categoryList = IntStream.range(0, 3)
                .mapToObj(i -> new Category("AKK10" + i, "CT_" + i, true, null))
                .collect(Collectors.toList());
        //when
        Mockito.when(categoryService.findAll()).thenReturn(categoryList);
        //then
        Assert.assertEquals(categoryList, categoryService.findAll());
        //verify
        Mockito.verify(categoryService, times(1)).findAll();
        Mockito.verifyNoMoreInteractions(categoryService);
    }

    @Test
    public void whenFindByCategoryCode_shouldReturnCategoryObject(){
        //when
        Mockito.when(categoryService.findByCategoryCode(any(String.class))).thenReturn(Optional.ofNullable(category));
        //then
        Assert.assertEquals(Optional.ofNullable(category), categoryService.findByCategoryCode("CT_1"));
        //verify
        Mockito.verify(categoryService, times(1)).findByCategoryCode(any(String.class));
        Mockito.verifyNoMoreInteractions(categoryService);
    }
    @Test
    public void whenFindByCategoryCode_shouldReturnNull(){
        //when
        Mockito.when(categoryService.findByCategoryCode(any(String.class))).thenReturn(null);
        //then
        Assert.assertEquals(null, categoryService.findByCategoryCode("CT_1"));
        //verify
        Mockito.verify(categoryService, times(1)).findByCategoryCode(any(String.class));
        Mockito.verifyNoMoreInteractions(categoryService);
    }

//    @Test
//    public void givenCategoryObject_whenUpdateCategory_thenReturnUpdatedCategory(){
//        // given - precondition or setup
//        given(categoryService.updateCategory(category)).willReturn(category);
//        category.setCategoryCode("AGFJ1");
//        category.setCategoryName("CT_1");
//        category.setProducts(null);
//        // when -  action or the behaviour that we are going test
//        Category updatedCategory = categoryService.updateCategory(category);
//
//        // then - verify the output
//        assertThat(updatedCategory.getCategoryCode()).isEqualTo("AGFJ1");
//        assertThat(updatedCategory.getCategoryName()).isEqualTo("CT_1");
////        assertThat(updatedCategory.getProducts()).isEqualTo(null);
//    }

    @Test
    public void givenCategoryObject_whenSaveCategory_thenReturnCategoryObject(){
        // given - precondition or setup
        given(categoryService.findByCategoryCode(category.getCategoryCode()))
                .willReturn(Optional.empty());

        given(categoryService.createCategory(category)).willReturn(category);

        System.out.println(categoryRepository);
        System.out.println(categoryService);

        // when -  action or the behaviour that we are going test
        Category createdCategory = categoryService.createCategory(category);

        System.out.println(createdCategory);
        // then - verify the output
        assertThat(String.valueOf(createdCategory));
        Mockito.verify(categoryService, times(1)).createCategory(category);
    }

    @Test
    public void givenCategoryObject_whenSaveCategory_thenReturnCategoryNull(){
        // given - precondition or setup
        given(categoryService.findByCategoryCode(category.getCategoryCode()))
                .willReturn(Optional.empty());

        given(categoryService.createCategory(category)).willReturn(null);

        System.out.println(categoryRepository);
        System.out.println(categoryService);

        // when -  action or the behaviour that we are going test
        Category createdCategory = categoryService.createCategory(category);

        System.out.println(createdCategory);
        // then - verify the output
        Assert.assertEquals(null, categoryService.createCategory(null));
        Mockito.verify(categoryService, times(1)).createCategory(category);
    }

    private JsonPathAssertions assertThat(String categoryCode) {
        return null;
    }

    @Test
    public void givenCategoryCode_whenDeleteCategory_thenNothing(){
        // given - precondition or setup
        long categoryCode = 1L;

        willDoNothing().given(categoryService).deleteCategory(category);

        // when -  action or the behaviour that we are going test
        categoryService.deleteCategory(category);

        // then - verify the output
        Mockito.verify(categoryService, times(1)).deleteCategory(category);
    }
}
