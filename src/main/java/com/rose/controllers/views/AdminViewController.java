package com.rose.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {
    @GetMapping("/admin/accounts")
    public String adminProduct(){
        return "admin/pages/account";
    }

    @GetMapping("/admin/clothes")
    public String adminClothes(){
        return "admin/pages/clothes";
    }

    @GetMapping("/admin/category")
    public String adminCategory(){
        return "admin/pages/category";
    }


    @GetMapping("/admin/color")
    public String adminColor(){
        return "admin/pages/color";
    }

    @GetMapping("/admin/author")
    public String adminAuthor() {
        return "admin/pages/authorizing";
    }

    @GetMapping("/admin/size")
    public String adminSize() {
        return "admin/pages/size";
    }

    @GetMapping("/admin/order-details")
    public String adminOrderDetails() {
        return "admin/pages/order-details";
    }

    @GetMapping("/admin/products-details")
    public String adminProductsDetails() {
        return "admin/pages/products-details";
    }

    @GetMapping("/admin/content-blogs")
    public String adminContentBlogs() {
        return "admin/pages/content-blogs";
    }

    @GetMapping("/admin/discount")
    public String adminDiscount() {
        return "admin/pages/discount";
    }
}
