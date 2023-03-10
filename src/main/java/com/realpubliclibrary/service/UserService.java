package com.realpubliclibrary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realpubliclibrary.dao.RealPublicLibraryDB;
import com.realpubliclibrary.model.AuthorDetails;
import com.realpubliclibrary.model.BookedRoomDetails;
import com.realpubliclibrary.model.EventDetails;
import com.realpubliclibrary.model.LoggedInUser;
import com.realpubliclibrary.model.RegUser;
import com.realpubliclibrary.model.RentalDetails;
import com.realpubliclibrary.model.SponsorDetails;
import com.realpubliclibrary.model.UserDetails;
import com.realpubliclibrary.model.VerifyEmail;
import com.realpubliclibrary.model.PasswordReset;
import com.realpubliclibrary.model.PopularBookGenre;
import com.realpubliclibrary.model.PopularBooks;

@Service
public class UserService {
	
	@Autowired
	RealPublicLibraryDB realPublicLibraryDBImpl;
	
	public LoggedInUser validateUser(String emailId, String password) {
		return realPublicLibraryDBImpl.validateUser(emailId, password);
	}
	
	public RegUser reguser(String fname,String lname,String phone,String email,String street,String state,
			String zipcode,String idtype,String idnumber,String password) {
		return realPublicLibraryDBImpl.reguser(fname, lname,
		    	phone,email, street,state,zipcode,idtype,idnumber,password);
	}
	
	public VerifyEmail verifyemail(String email) {
		return realPublicLibraryDBImpl.verifyemail(email);
	}
	
	public PasswordReset pwdreset(String inputotp,String newpwd, String useremail) {
		return realPublicLibraryDBImpl.pwdreset(inputotp,newpwd,useremail);
	}
	
	public List<UserDetails> getCustomers() {
		return realPublicLibraryDBImpl.getCustomers();
	}
	
	public List<RentalDetails> getRentalDetails(String cusId) {
		return realPublicLibraryDBImpl.getRentalDetails(cusId);
	}
	
	public List<BookedRoomDetails> getBookedRoomDetails(String cusId) {
		return realPublicLibraryDBImpl.getBookedRoomDetails(cusId);
	}
	
	public List<BookedRoomDetails> getRoomAvailability(String selectedDate, String numOfPeople) {
		return realPublicLibraryDBImpl.getRoomAvailability(selectedDate, numOfPeople);
	}
	
	public boolean bookRoom(String cusId, String roomId, String slot, String selectedDate) {
		return realPublicLibraryDBImpl.bookRoom(cusId, roomId, slot, selectedDate);
	}
	
	public boolean cancelRoomReservation(String reservationId, String cusId) {
		return realPublicLibraryDBImpl.cancelRoomReservation(reservationId, cusId);
	}
	
	public List<EventDetails> getEvents(String userType) {
		return realPublicLibraryDBImpl.getEvents(userType);
	}
	
	public List<EventDetails> getCustExhibition(String cusId) {
		return realPublicLibraryDBImpl.getCustExhibition(cusId);
	}
	
	public boolean bookExhibition(String cusId, String exhibitionId) {
		return realPublicLibraryDBImpl.bookExhibition(cusId, exhibitionId);
	}
	
	public boolean cancelCustExhBooking(String cusId, String eventId) {
		return realPublicLibraryDBImpl.cancelCustExhBooking(cusId, eventId);
	}
	
	public boolean returnBook(String rentalId) {
		return realPublicLibraryDBImpl.returnBook(rentalId);
	}
	
	public List<SponsorDetails> getSeminarSponsors(String eventId) {
		return realPublicLibraryDBImpl.getSeminarSponsors(eventId);
	}
	
	public List<AuthorDetails> getAuthors(String eventId) {
		return realPublicLibraryDBImpl.getAuthors(eventId);
	}
	
	public boolean sendSeminarInvitation(String eventId, String authorId) {
		return realPublicLibraryDBImpl.sendSeminarInvitation(eventId, authorId);
	}
	
	public List<PopularBooks> getPopularBooks() {
		return realPublicLibraryDBImpl.getPopularBooks();
	}
	
	public List<PopularBookGenre> getPopularBookGenre() {
		return realPublicLibraryDBImpl.getPopularBookGenre();
	}
	
}
