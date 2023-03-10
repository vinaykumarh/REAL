package com.realpubliclibrary.model;

public class VerifyEmail {
	
	
	private String email;
	
	private String user_otp;
	
	private String cusid;
	
	private String userType;
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getUserOtp() {
		return user_otp;
	}

	public void setUserOtp(String user_otp) {
		this.user_otp = user_otp;
	}
	
	public String getCusId() {
		return cusid;
	}

	public void setCusId(String cusid) {
		this.cusid = cusid;
	}
	
	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	
}
