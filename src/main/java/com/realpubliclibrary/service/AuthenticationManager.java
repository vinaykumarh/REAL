package com.realpubliclibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationManager {
    	@Autowired
	private PasswordEncoder passWordEncDec ;

	public String passwordEncryptor(String rawPassword) {
		String encryptedPassword = passWordEncDec.encode(rawPassword);
		return encryptedPassword;
	}

	public boolean passwordChecker(String rawPassword, String encryptedPassword) {
		if (passWordEncDec.matches(rawPassword, encryptedPassword)) {
			return true;
		} else {
			return false;
		    }
		}

}
