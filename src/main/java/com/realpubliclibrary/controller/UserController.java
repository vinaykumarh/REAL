package com.realpubliclibrary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realpubliclibrary.model.AuthorDetails;
import com.realpubliclibrary.model.BookedRoomDetails;
import com.realpubliclibrary.model.EventDetails;
import com.realpubliclibrary.model.PopularBookGenre;
import com.realpubliclibrary.model.PopularBooks;
import com.realpubliclibrary.model.RentalDetails;
import com.realpubliclibrary.model.SponsorDetails;
import com.realpubliclibrary.model.UserDetails;
import com.realpubliclibrary.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping(value = "/getcustomers", method = RequestMethod.GET)
	public List<UserDetails> getCustomers() {
		return userService.getCustomers();
	}

	@RequestMapping(value = "/getrentaldetails", method = RequestMethod.GET)
	public List<RentalDetails> getRentalDetails(@RequestParam String cusId) {
		return userService.getRentalDetails(cusId);
	}

	@RequestMapping(value = "/bookedroomdetails", method = RequestMethod.GET)
	public List<BookedRoomDetails> getBookedRoomDetails(@RequestParam String cusId) {
		return userService.getBookedRoomDetails(cusId);
	}

	@RequestMapping(value = "/getroomavailability", method = RequestMethod.GET)
	public List<BookedRoomDetails> getRoomAvailability(@RequestParam String selectedDate,
			@RequestParam String numOfPeople) {
		return userService.getRoomAvailability(selectedDate, numOfPeople);
	}

	@RequestMapping(value = "/bookroom", method = RequestMethod.GET)
	public boolean bookRoom(@RequestParam String cusId, @RequestParam String roomId, @RequestParam String slot,
			@RequestParam String selectedDate) {
		return userService.bookRoom(cusId, roomId, slot, selectedDate);
	}

	@RequestMapping(value = "/cancelroomreservation", method = RequestMethod.GET)
	public boolean cancelRoomReservation(@RequestParam String reservationId, @RequestParam String cusId) {
		return userService.cancelRoomReservation(reservationId, cusId);
	}

	@RequestMapping(value = "/getevents", method = RequestMethod.GET)
	public List<EventDetails> getEvents(@RequestParam String userType) {
		return userService.getEvents(userType);
	}

	@RequestMapping(value = "/getcustexhibition", method = RequestMethod.GET)
	public List<EventDetails> getCustExhibition(@RequestParam String cusId) {
		return userService.getCustExhibition(cusId);
	}

	@RequestMapping(value = "/bookexhibition", method = RequestMethod.GET)
	public boolean bookExhibition(@RequestParam String cusId, @RequestParam String exhibitionId) {
		return userService.bookExhibition(cusId, exhibitionId);
	}

	@RequestMapping(value = "/cancelcustexhbooking", method = RequestMethod.GET)
	public boolean cancelCustExhBooking(@RequestParam String cusId, @RequestParam String eventId) {
		return userService.cancelCustExhBooking(cusId, eventId);
	}

	@RequestMapping(value = "/returnbook", method = RequestMethod.GET)
	public boolean returnBook(@RequestParam String rentalId) {
		return userService.returnBook(rentalId);
	}

	@RequestMapping(value = "/getseminarsponsors", method = RequestMethod.GET)
	public List<SponsorDetails> getSeminarSponsors(@RequestParam String eventId) {
		return userService.getSeminarSponsors(eventId);
	}

	@RequestMapping(value = "/getauthors", method = RequestMethod.GET)
	public List<AuthorDetails> getAuthors(@RequestParam String eventId) {
		return userService.getAuthors(eventId);
	}
	
	@RequestMapping(value = "/sendseminarinvitation", method = RequestMethod.POST)
	public boolean sendSeminarInvitation(@RequestParam String eventId, @RequestParam String authorId) {
		return userService.sendSeminarInvitation(eventId, authorId);
	}
	
	@RequestMapping(value = "/getPopularBooks", method = RequestMethod.GET)
	public List<PopularBooks> getPopularBooks() {
		return userService.getPopularBooks();
	}
	
	@RequestMapping(value = "/getPopularBookGenre", method = RequestMethod.GET)
	public List<PopularBookGenre> getPopularBookGenre() {
		return userService.getPopularBookGenre();
	}


}
