package com.realpubliclibrary.model;

public class PasswordReset {
	
	
	private String email;
	
	private String inputotp;
	
	private String newpwd;
	
	private String cusid;
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getInputOtp() {
		return inputotp;
	}

	public void setInputOtp(String inputotp) {
		this.inputotp = inputotp;
	}
	
	public String getnewPwd() {
		return newpwd;
	}

	public void setnewPwd(String newpwd) {
		this.newpwd = newpwd;
	}
	
	public String getcusID() {
		return cusid;
	}

	public void setcusID(String cusid) {
		this.cusid = cusid;
	}
	
}
