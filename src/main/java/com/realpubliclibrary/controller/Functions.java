package com.realpubliclibrary.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Functions {

	@RequestMapping("/home")
	public String getHomepage() {
		return "forward:/index.html";
	}
	
	@RequestMapping("/")
    public String getLoginpage() {
        return "forward:/login.html";
    }
}
