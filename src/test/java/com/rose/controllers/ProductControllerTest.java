//package com.rose.controllers;
//
//import com.rose.entities.Product;
//import com.rose.repositories.ProductSearchRepository;
//import com.rose.services.impl.ProductEntryServiceImpl;
//import com.rose.services.impl.ProductServiceImpl;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.awt.print.Pageable;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.hamcrest.CoreMatchers.is;
//
//@WebMvcTest(value =ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@RunWith(SpringRunner.class)
//public class ProductControllerTest {
//    @MockBean
//    private ProductServiceImpl productService;
//    @MockBean
//    private ProductEntryServiceImpl productEntryService;
//
//    @MockBean
//    private ProductSearchRepository productSearchRepository;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void givenListOfProducts_whenGetAllProducts_thenReturnProductList() throws Exception {
//        List<Product> productList = Arrays.asList(
//                new Product("SP001", "Sản phẩm 1", true, null, null, "https://scontent.fsgn5-11.fna.fbcdn.net/v/t39.30808-1/307917691_1212278942836462_268938670643155590_n.jpg?stp=dst-jpg_p320x320&_nc_cat=110&ccb=1-7&_nc_sid=7206a8&_nc_ohc=qqV4RpSPfSsAX-84ot0&_nc_ht=scontent.fsgn5-11.fna&oh=00_AfDeqDeyPnq6hVSqmYyNGeb6TNa3nx7idhaR4-xpybjDaQ&oe=6396341E",
//                        19.00, null, null),
//                new Product("SP002", "Sản phẩm 2", true, null, null, "https://scontent.fsgn5-11.fna.fbcdn.net/v/t39.30808-1/307917691_1212278942836462_268938670643155590_n.jpg?stp=dst-jpg_p320x320&_nc_cat=110&ccb=1-7&_nc_sid=7206a8&_nc_ohc=qqV4RpSPfSsAX-84ot0&_nc_ht=scontent.fsgn5-11.fna&oh=00_AfDeqDeyPnq6hVSqmYyNGeb6TNa3nx7idhaR4-xpybjDaQ&oe=6396341E",
//                        19.00, null, null),
//                new Product("SP003", "Sản phẩm 3", false, null, null, "https://scontent.fsgn5-11.fna.fbcdn.net/v/t39.30808-1/307917691_1212278942836462_268938670643155590_n.jpg?stp=dst-jpg_p320x320&_nc_cat=110&ccb=1-7&_nc_sid=7206a8&_nc_ohc=qqV4RpSPfSsAX-84ot0&_nc_ht=scontent.fsgn5-11.fna&oh=00_AfDeqDeyPnq6hVSqmYyNGeb6TNa3nx7idhaR4-xpybjDaQ&oe=6396341E",
//                19.00, null, null));
//        Mockito.when(productService.getAll()).thenReturn(productList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/api/v2/products"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.status", is("OK")))
//                .andExpect(jsonPath("$.message", is("Query successfully...")))
//                .andExpect(jsonPath("$.data.*", hasSize(2)))
//                .andExpect(jsonPath("$.data[0].productCode", is(productList.get(0).getProductCode())))
//                .andExpect(jsonPath("$.data[0].available", is(productList.get(0).getAvailable())))
//                .andExpect(jsonPath("$.data[1].productCode", is(productList.get(1).getProductCode())))
//                .andExpect(jsonPath("$.data[1].available", is(productList.get(1).getAvailable())));
//        Mockito.verify(productService, Mockito.times(1)).getAll();
//        Mockito.verifyNoMoreInteractions(productService);
//    }
//}
