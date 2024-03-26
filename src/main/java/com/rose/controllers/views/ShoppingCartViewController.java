package com.rose.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cart")
public class ShoppingCartViewController {
	
	@GetMapping("/view")
	public String view() {
		return "user/pages/shopping-cart";
	}

	@GetMapping("/checkout")
	public String checkout() {
		return "user/pages/checkout";
	}
}
