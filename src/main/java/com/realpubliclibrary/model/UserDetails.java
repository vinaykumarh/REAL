package com.realpubliclibrary.model;

public class UserDetails {
	
	private String cusId;
	
	private String firstName;
	
	private String lastName;
	
	private String phoneNo;
	
	private String email;
	
	private String hasBookReserved;
	
	private String hasRoomReserved;

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHasBookReserved() {
		return hasBookReserved;
	}

	public void setHasBookReserved(String hasBookReserved) {
		this.hasBookReserved = hasBookReserved;
	}

	public String getHasRoomReserved() {
		return hasRoomReserved;
	}

	public void setHasRoomReserved(String hasRoomReserved) {
		this.hasRoomReserved = hasRoomReserved;
	}

}
