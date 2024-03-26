package com.rose.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductViewController {

    @GetMapping("/shop")
    public String list() {
        return "user/pages/shop";
    }

    @GetMapping("/product-detail/{id}")
    public String detail(@PathVariable String id) {
        return "user/pages/detail";
    }
}

