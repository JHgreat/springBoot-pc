package com.ch.pc.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
public class MainController {
	
	
	@RequestMapping("/main/main.do")
	public String main() {
		return "/main/main";
	}
	
	
}