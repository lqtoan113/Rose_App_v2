package com.rose.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/security")
public class SecurityViewController {
	
	@GetMapping("/login/form")
	public String loginForm(Model model) {
		model.addAttribute("message", "Please login!");
		return "user/security/login";
	}

	@GetMapping("/page/404")
	public String page404(Model model) {
		return "user/security/404-page";
	}
	@GetMapping("/page/403")
	public String page403(Model model) {
		return "user/security/403-page";
	}
	@GetMapping("/page/401")
	public String page401(Model model) {
		return "user/security/401-page";
	}

	@GetMapping("/reset-password/**")
	public String resetPassword(){
		return "user/security/reset-password";
	}
}
