package com.rose.controllers.views;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeViewController {
	
	@GetMapping({""})
	public String home() {
		return "user/pages/home";
	}
	
	@GetMapping({"/admin","/admin/home/index"})
	public String admin() {
		return "/admin/pages/home";
	}

	@GetMapping("/forget-password")
	public String forgetPassword(){
		return "user/security/forget-password";
	}

	@GetMapping("/reset-password")
	public String resetPassword(){
		return "user/security/reset-password";
	}

	@GetMapping("/change-password")
	public String changePassword() {
		return "user/security/change-password";
	}

	@GetMapping("/blog")
	public String blog(){
		return "user/pages/blog";
	}

	@GetMapping("/about")
	public String about(){
		return "user/pages/about";
	}

	@GetMapping("/contact")
	public String contact(){
		return "user/pages/contact";
	}

	@GetMapping("/recharge")
	public String recharge(){
		return "user/pages/recharge";
	}
}
