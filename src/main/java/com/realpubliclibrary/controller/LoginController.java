package com.realpubliclibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realpubliclibrary.model.LoggedInUser;
import com.realpubliclibrary.model.RegUser;
import com.realpubliclibrary.model.VerifyEmail;
import com.realpubliclibrary.model.PasswordReset;
import com.realpubliclibrary.service.UserService;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	 @Autowired
	 UserService userService;
	 
	@RequestMapping(value="/validateuser", method=RequestMethod.GET)
	public LoggedInUser validateUser(@RequestParam String emailId, @RequestParam String password) {
	    return userService.validateUser(emailId, password);
	}
	@RequestMapping(value="/reguser", method=RequestMethod.GET)
	public RegUser reguser(@RequestParam String fname, @RequestParam String lname,
			@RequestParam String phone, @RequestParam String email,
			@RequestParam String street, @RequestParam String state,
			@RequestParam String zipcode, @RequestParam String idtype,
			@RequestParam String idnumber, @RequestParam String password) {
	    return userService.reguser(fname, lname,
	    	phone,email, street,state,zipcode,idtype,idnumber,password);
	}
	@RequestMapping(value="/verifyemail", method=RequestMethod.GET)
	public VerifyEmail reguser(@RequestParam String email) {
	    return userService.verifyemail(email);
	}
	
	@RequestMapping(value="/pwdreset", method=RequestMethod.GET)
	public PasswordReset pwdreset(@RequestParam String inputotp,@RequestParam String newpwd,@RequestParam String useremail) {
	    return userService.pwdreset(inputotp,newpwd,useremail);
	}
	
}
