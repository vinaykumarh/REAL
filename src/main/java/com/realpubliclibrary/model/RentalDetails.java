package com.realpubliclibrary.model;

public class RentalDetails {
	
	private String rentalId;
	
	private String bookId;
	
	private String bookName;
	
	private String borrowedDate;
	
	private String expReturnDate;
	
	private String isReturned;

	public String getIsReturned() {
	    return isReturned;
	}

	public void setIsReturned(String isReturned) {
	    this.isReturned = isReturned;
	}

	public String getRentalId() {
		return rentalId;
	}

	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBorrowedDate() {
		return borrowedDate;
	}

	public void setBorrowedDate(String borrowedDate) {
		this.borrowedDate = borrowedDate;
	}

	public String getExpReturnDate() {
		return expReturnDate;
	}

	public void setExpReturnDate(String expReturnDate) {
		this.expReturnDate = expReturnDate;
	}
}
