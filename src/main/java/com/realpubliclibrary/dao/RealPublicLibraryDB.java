package com.realpubliclibrary.dao;

import java.util.List;

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

public interface RealPublicLibraryDB {
	
	public LoggedInUser validateUser(String emailId, String password);
	
	public RegUser reguser (String fname,String lname,String phone,String email,String street,String state,
			String zipcode,String idtype,String idnumber,String password);
	
	public VerifyEmail verifyemail(String email);
	
	public PasswordReset pwdreset(String inputotp,String newpwd, String useremail);
	
	public List<UserDetails> getCustomers();
	
	public List<RentalDetails> getRentalDetails(String cusId);
	
	public List<BookedRoomDetails> getBookedRoomDetails(String cusId);
	
	public List<BookedRoomDetails> getRoomAvailability(String selectedDate, String numOfPeople);
	
	public boolean bookRoom(String cusId, String roomId, String slot, String selectedDate);
	
	public boolean cancelRoomReservation(String reservationId, String cusId);
	
	public List<EventDetails> getEvents(String userType);
	
	public List<EventDetails> getCustExhibition(String cusId);
	
	public boolean bookExhibition(String cusId, String exhibitionId);
	
	public boolean cancelCustExhBooking(String cusId, String eventId);
	
	public boolean returnBook(String rentalId);
	
	public List<SponsorDetails> getSeminarSponsors(String eventId);
	
	public List<AuthorDetails> getAuthors(String eventId);
	
	public boolean sendSeminarInvitation(String eventId, String authorEmail);
	
	public List<PopularBooks> getPopularBooks();
	
	public List<PopularBookGenre> getPopularBookGenre();
}
