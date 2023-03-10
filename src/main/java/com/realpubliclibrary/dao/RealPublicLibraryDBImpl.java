package com.realpubliclibrary.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.realpubliclibrary.email.EmailUtility;
import com.realpubliclibrary.model.AuthorDetails;
import com.realpubliclibrary.model.BookedRoomDetails;
import com.realpubliclibrary.model.EventDetails;
import com.realpubliclibrary.model.LoggedInUser;
import com.realpubliclibrary.model.PasswordReset;
import com.realpubliclibrary.model.PopularBookGenre;
import com.realpubliclibrary.model.PopularBooks;
import com.realpubliclibrary.model.RegUser;
import com.realpubliclibrary.model.RentalDetails;
import com.realpubliclibrary.model.SeminarInvitationDetails;
import com.realpubliclibrary.model.SponsorDetails;
import com.realpubliclibrary.model.UserDetails;
import com.realpubliclibrary.model.VerifyEmail;
import com.realpubliclibrary.resources.ReadPropertiesFile;
import com.realpubliclibrary.service.AuthenticationManager;

import oracle.jdbc.OracleTypes;

@Repository
public class RealPublicLibraryDBImpl implements RealPublicLibraryDB {
    @Autowired
    private AuthenticationManager authenticationManager;

    private static Properties properties = ReadPropertiesFile.readPropertiesFile("application.properties");

    @Override
    @Transactional
    public LoggedInUser validateUser(String emailId, String password) {
	LoggedInUser userDetail = new LoggedInUser();

	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));
	    String call = "{call VALIDATE_USER(?,?,?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setQueryTimeout(1800);
	    cstmt.setString(1, emailId);
	    cstmt.registerOutParameter(2, OracleTypes.VARCHAR);
	    cstmt.registerOutParameter(3, OracleTypes.VARCHAR);
	    cstmt.executeUpdate();

	    String userType = cstmt.getString(3);
	    userDetail.setUserType(userType);
	    if (authenticationManager.passwordChecker(password, cstmt.getString(2))) {
		if (userType.equals("CUS")) {
		    PreparedStatement pst = con.prepareStatement("select CUS_ID from customer where c_email = ?");
		    pst.setString(1, emailId);
		    ResultSet rs = pst.executeQuery();
		    if (rs.next())
			userDetail.setCusId(rs.getString("CUS_ID"));
		} else if (userType.equals("EMP")) {
		    PreparedStatement pst = con.prepareStatement("select EMP_ID from EMPLOYEE where E_EMAIL = ?");
		    pst.setString(1, emailId);
		    ResultSet rs = pst.executeQuery();
		    if (rs.next())
			userDetail.setEmpId(rs.getString("EMP_ID"));
		} else {
		    userDetail.setCusId("0");
		    userDetail.setEmpId("0");
		}
	    } else {
		userDetail.setUserType("NA");
		userDetail.setCusId("0");
		userDetail.setEmpId("0");
	    }
	    return userDetail;
	} catch (Exception e) {
	    System.out.println(e);
	    userDetail.setUserType("NA");
	    userDetail.setCusId("0");
	    userDetail.setEmpId("0");
	}
	return userDetail;
    }

    @Override
    @Transactional
    public VerifyEmail verifyemail(String email) {
	VerifyEmail fgtpwddetails = new VerifyEmail();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call verify_email(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setQueryTimeout(1800);
	    cstmt.setString(1, email);

	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);

	    while (rs.next()) {
		fgtpwddetails.setUserType(rs.getString("user_type"));
		String UserType = fgtpwddetails.getUserType();
		if (UserType.equals("CUS")) {
		    fgtpwddetails.setCusId(rs.getString("CUS_ID"));
		    fgtpwddetails.setUserOtp(rs.getString("C_OTP"));
		    String UserOPT = fgtpwddetails.getUserOtp();
		    fgtpwddetails.setEmail(rs.getString("C_EMAIL"));
		    String UserEmail = fgtpwddetails.getEmail();

		    EmailUtility emailUtil = new EmailUtility();
		    String mailTxt = "OTP for reset is :" + UserOPT;
		    String mailSubject = "OTP Pwd reset ";
		    emailUtil.sendEmail(UserEmail, mailSubject, mailTxt);
		} else if (UserType.equals("EMP")) {
		    fgtpwddetails.setCusId(rs.getString("EMP_ID"));
		    fgtpwddetails.setUserOtp(rs.getString("E_OTP"));
		    String UserOPT = fgtpwddetails.getUserOtp();
		    fgtpwddetails.setEmail(rs.getString("E_EMAIL"));
		    String UserEmail = fgtpwddetails.getEmail();

		    EmailUtility emailUtil = new EmailUtility();
		    String mailTxt = "OTP for reset is :" + UserOPT;
		    String mailSubject = "OTP Pwd reset ";
		    emailUtil.sendEmail(UserEmail, mailSubject, mailTxt);
		}

	    }
	    con.close();

	} catch (Exception e) {
	    System.out.println(e);
	}
	return fgtpwddetails;
    }

    @Override
    @Transactional
    public PasswordReset pwdreset(String inputotp, String newpwd, String useremail) {
	PasswordReset pwdresetdetails = new PasswordReset();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call update_pwd(?, ?, ?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setQueryTimeout(1800);
	    cstmt.setString(1, inputotp);
	    cstmt.setString(2, authenticationManager.passwordEncryptor(newpwd));
	    cstmt.setString(3, useremail);

	    cstmt.registerOutParameter(4, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(4);

	    while (rs.next()) {
		pwdresetdetails.setEmail(rs.getString("C_EMAIL"));
		String UserEmail = pwdresetdetails.getEmail();
		EmailUtility emailUtil = new EmailUtility();

		String mailTxt = "Password is reset";
		String mailSubject = " Password reset complete  ";

		emailUtil.sendEmail(UserEmail, mailSubject, mailTxt);
	    }
	    con.close();
	}

	catch (Exception e) {
	    System.out.println(e);

	}
	return pwdresetdetails;
    }

    @Override
    @Transactional
    public List<UserDetails> getCustomers() {
	List<UserDetails> userDetailsret = new ArrayList<UserDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call GET_CUSTOMER(?) }";
	    CallableStatement cstmt = con.prepareCall(call);

	    cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(1);
	    while (rs.next()) {
		UserDetails userDetailsTmp = new UserDetails();

		userDetailsTmp.setCusId(rs.getString("Cus_ID"));
		userDetailsTmp.setFirstName(rs.getString("C_FNAME"));
		userDetailsTmp.setLastName(rs.getString("C_LNAME"));
		userDetailsTmp.setPhoneNo(rs.getString("C_PH_NO"));
		userDetailsTmp.setEmail(rs.getString("C_EMAIL"));
		userDetailsTmp.setHasBookReserved(rs.getString("HAS_BOOK_RENTED"));
		userDetailsTmp.setHasRoomReserved(rs.getString("HAS_ROOM_BOOKED"));
		userDetailsret.add(userDetailsTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return userDetailsret;
    }

    @Override
    @Transactional
    public List<RentalDetails> getRentalDetails(String cusId) {
	List<RentalDetails> rentalDetailsret = new ArrayList<RentalDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call CUST_BOOK_RENTAL_DETAILS(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, cusId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		RentalDetails rentalDetailsTmp = new RentalDetails();
		rentalDetailsTmp.setRentalId(rs.getString("RENTAL_ID"));
		rentalDetailsTmp.setBookId(rs.getString("BOOK_INV_ID"));
		rentalDetailsTmp.setBookName(rs.getString("B_NAME"));
		rentalDetailsTmp.setBorrowedDate(rs.getString("BORROW_DATE"));
		rentalDetailsTmp.setExpReturnDate(rs.getString("EXP_RET_DATE"));
		rentalDetailsTmp.setIsReturned(rs.getString("IS_RETURNED"));
		rentalDetailsret.add(rentalDetailsTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return rentalDetailsret;
    }

    @Override
    @Transactional
    public List<BookedRoomDetails> getBookedRoomDetails(String cusId) {

	List<BookedRoomDetails> bookedRoomDetails = new ArrayList<BookedRoomDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call GET_RESERVED_ROOMS(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, cusId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		BookedRoomDetails bookedRoomDetailsTmp = new BookedRoomDetails();
		bookedRoomDetailsTmp.setRoomId(rs.getString("ROOM_ID"));
		bookedRoomDetailsTmp.setCusId(cusId);
		List<String> slots = new ArrayList<String>();
		slots.add(rs.getString("SLOT"));
		bookedRoomDetailsTmp.setSlots(slots);
		bookedRoomDetailsTmp.setReservationDate(rs.getString("RES_DATE"));
		bookedRoomDetailsTmp.setReservationId(rs.getString("RESERVATION_ID"));
		bookedRoomDetails.add(bookedRoomDetailsTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return bookedRoomDetails;
    }

    @Override
    @Transactional
    public List<BookedRoomDetails> getRoomAvailability(String selectedDate, String numOfPeople) {
	List<BookedRoomDetails> roomAvailableDetails = new ArrayList<BookedRoomDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));
	    String call = "{call GET_FREE_ROOMS(?, ?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, selectedDate);
	    cstmt.setString(2, numOfPeople);
	    cstmt.registerOutParameter(3, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(3);
	    while (rs.next()) {
		BookedRoomDetails roomAvailableDetailsTmp = new BookedRoomDetails();
		roomAvailableDetailsTmp.setRoomId(rs.getString("ROOM_ID"));
		List<String> convertedSlotsList = Stream.of(rs.getString("SLOT").split(",", -1))
			.collect(Collectors.toList());
		List<String> slots = new ArrayList<String>();
		slots.addAll(convertedSlotsList);

		roomAvailableDetailsTmp.setSlots(slots);
		roomAvailableDetailsTmp.setCapacity(rs.getString("MAX_CAP"));
		roomAvailableDetails.add(roomAvailableDetailsTmp);
	    }
	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return roomAvailableDetails;
    }

    @Override
    @Transactional
    public boolean bookRoom(String cusId, String roomId, String slot, String selectedDate) {

	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    PreparedStatement pst = con.prepareStatement(
		    "select * from reservation where reservation.room_id=? and reservation.slot=? and trunc(res_date)=trunc(to_date(?,'YYYY-MM-DD'))");
	    pst.setString(1, roomId);
	    pst.setString(2, slot);
	    pst.setString(3, selectedDate);
	    ResultSet rs = pst.executeQuery();
	    if (rs.next())
		return false;

	    String call = "{call RESERVE_ROOM(?, ?, ?, ?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, selectedDate);
	    cstmt.setString(2, slot);
	    cstmt.setString(3, roomId);
	    cstmt.setString(4, cusId);
	    cstmt.registerOutParameter(5, OracleTypes.INTEGER);
	    cstmt.executeUpdate();
	    int resId = cstmt.getInt(5);

	    String reservationID = Integer.toString(resId);
	    String mailTxt = "Your Room has been reserved on " + selectedDate + "\t\n RESERVATION ID = "
		    + reservationID;
	    String mailSubject = "Room " + roomId + " has been reserved ";

	    sendEmail(con, cusId, mailSubject, mailTxt);

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Transactional
    private void sendEmail(Connection con, String cusId, String mailSubject, String mailTxt) throws SQLException {
	EmailUtility emailUtil = new EmailUtility();
	PreparedStatement pst1 = con.prepareStatement("select c_email from customer where cus_id =? ");
	pst1.setString(1, cusId);
	ResultSet rs2 = pst1.executeQuery();
	String cusEmail = "";
	if (rs2.next())
	    cusEmail = rs2.getString("c_email");
	emailUtil.sendEmail(cusEmail, mailSubject, mailTxt);
    }

    @Override
    @Transactional
    public boolean cancelRoomReservation(String reservationId, String cusId) {
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));
	    PreparedStatement pst = con.prepareStatement("delete from RESERVATION where RESERVATION_ID=?");
	    pst.setString(1, reservationId);
	    pst.execute();

	    String mailTxt = "Your Room Reservation has been Cancelled" + "\t\n RESERVATION ID = " + reservationId;
	    String mailSubject = "Room reservation has been Cancelled ";
	    sendEmail(con, cusId, mailSubject, mailTxt);

	    con.close();

	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Override
    @Transactional
    public List<EventDetails> getEvents(String userType) {
	List<EventDetails> exhibitionDetails = new ArrayList<EventDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));
	    String call = "{call GET_EVENTS(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, userType);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		EventDetails eventDetailsTmp = new EventDetails();
		String eventType = rs.getString("EVENT_TYPE");
		if (userType.equals("CUS")) {
		    if (eventType.equals("E")) {
			eventDetailsTmp.setEventId(rs.getString("EVENT_ID"));
			eventDetailsTmp.setEventName(rs.getString("EVENT_NAME"));
			eventDetailsTmp.setStartDate(rs.getString("EVENT_START"));
			eventDetailsTmp.setEndDate(rs.getString("EVENT_STOP"));
			eventDetailsTmp.setGenre(rs.getString("TOPIC_NAME"));
			eventDetailsTmp.setEventType(eventType);
			exhibitionDetails.add(eventDetailsTmp);
		    }
		} else {
		    if (eventType.equals("E")) {
			eventDetailsTmp.setEventId(rs.getString("EVENT_ID"));
			eventDetailsTmp.setEventName(rs.getString("EVENT_NAME"));
			eventDetailsTmp.setEventType(eventType);
			eventDetailsTmp.setGenre(rs.getString("TOPIC_NAME"));
			eventDetailsTmp.setStartDate(rs.getString("EVENT_START"));
			eventDetailsTmp.setEndDate(rs.getString("EVENT_STOP"));
			eventDetailsTmp.setExhibitionExpense(rs.getString("EXPENSE"));
			exhibitionDetails.add(eventDetailsTmp);
		    }
		    if (eventType.equals("S")) {
			eventDetailsTmp.setEventId(rs.getString("EVENT_ID"));
			eventDetailsTmp.setEventName(rs.getString("EVENT_NAME"));
			eventDetailsTmp.setEventType(eventType);
			eventDetailsTmp.setGenre(rs.getString("TOPIC_NAME"));
			eventDetailsTmp.setStartDate(rs.getString("EVENT_START"));
			eventDetailsTmp.setEndDate(rs.getString("EVENT_STOP"));
			exhibitionDetails.add(eventDetailsTmp);
		    }
		}

	    }
	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
	return exhibitionDetails;
    }

    @Override
    @Transactional
    public boolean bookExhibition(String cusId, String exhibitionId) {
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call RESERVE_EXHIBITION(?, ?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, cusId);
	    cstmt.setString(2, exhibitionId);
	    cstmt.registerOutParameter(3, OracleTypes.INTEGER);
	    cstmt.executeUpdate();
	    int resId = cstmt.getInt(3);

	    String reservationID = Integer.toString(resId);
	    String mailTxt = "Your Exhibition has been reserved. " + "\t\n RESERVATION ID = " + reservationID;
	    String mailSubject = "Exhibition has been booked ";

	    sendEmail(con, cusId, mailSubject, mailTxt);

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Override
    @Transactional
    public List<EventDetails> getCustExhibition(String cusId) {
	List<EventDetails> exhibitionDetails = new ArrayList<EventDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call GET_CUS_EXHIBITION(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, cusId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		EventDetails eventDetailsTmp = new EventDetails();
		String eventType = rs.getString("EVENT_TYPE");

		if (eventType.equals("E")) {
		    String eventStartDate = rs.getString("EVENT_START");
		    LocalDate dateObj = LocalDate.now();
		    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		    String todayDate = dateObj.format(dateFormatter);
		    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		    Date date1 = format.parse(eventStartDate.substring(0, 10));
		    Date date2 = format.parse(todayDate);
		    if (date1.compareTo(date2) >= 0) {
			eventDetailsTmp.setIsFutureEvent("Y");
		    } else {
			eventDetailsTmp.setIsFutureEvent("N");
		    }
		    eventDetailsTmp.setEventId(rs.getString("EVENT_ID"));
		    eventDetailsTmp.setEventName(rs.getString("EVENT_NAME"));
		    eventDetailsTmp.setStartDate(rs.getString("EVENT_START"));
		    eventDetailsTmp.setEndDate(eventStartDate);
		    eventDetailsTmp.setGenre(rs.getString("TOPIC_NAME"));
		    eventDetailsTmp.setEventType(eventType);
		    exhibitionDetails.add(eventDetailsTmp);
		}

	    }
	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}
	return exhibitionDetails;
    }

    @Override
    @Transactional
    public boolean cancelCustExhBooking(String cusId, String eventId) {
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call CANCEL_RESERVE_EXHIBITION(?, ?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, cusId);
	    cstmt.setString(2, eventId);
	    cstmt.registerOutParameter(3, OracleTypes.INTEGER);
	    cstmt.executeUpdate();
	    int resId = cstmt.getInt(3);

	    String reservationID = Integer.toString(resId);
	    String mailTxt = "Your Exhibition Reservation has been cancelled. " + "\t\n RESERVATION ID = "
		    + reservationID;
	    String mailSubject = "Reservation has been Cancelled";

	    sendEmail(con, cusId, mailSubject, mailTxt);

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Override
    @Transactional
    public boolean returnBook(String rentalId) {
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call BOOK_RETURN(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, rentalId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();

	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		String invoiceId = rs.getString("invoice_id");
		String cusId = rs.getString("cus_id");
		String returnDate = rs.getString("Act_ret_date");
		String amount = rs.getString("AMOUNT");

		String mailSubject = "Book has been returned. Invoice has been attached";
		String mailTxt = "Your Book has been returned. " + "\t\n INVOICE ID = " + invoiceId
			+ "\t\n RETURN DATE = " + returnDate + "\t\n TOTAL PAYABLE AMOUNT = " + amount;
		sendEmail(con, cusId, mailSubject, mailTxt);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Override
    @Transactional
    public List<SponsorDetails> getSeminarSponsors(String eventId) {
	List<SponsorDetails> sponsorDetailsret = new ArrayList<SponsorDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call GET_SPONSORS(?, ?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, eventId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		SponsorDetails sponsorDetailsTmp = new SponsorDetails();
		sponsorDetailsTmp.setSponsorId(rs.getString("SPONSOR_ID"));
		sponsorDetailsTmp.setEventId(rs.getString("EVENT_ID"));
		sponsorDetailsTmp.setSponsorName(rs.getString("SPONSOR_NAME"));
		sponsorDetailsTmp.setSponsorType(rs.getString("SPONSOR_TYPE"));
		sponsorDetailsTmp.setSponsorAmount(rs.getString("AMOUNT"));
		sponsorDetailsret.add(sponsorDetailsTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return sponsorDetailsret;
    }

    @Override
    @Transactional
    public RegUser reguser(String fname, String lname, String phone, String email, String street, String state,
	    String zipcode, String idtype, String idnumber, String password) {
	RegUser reguserDetails = new RegUser();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"), properties.getProperty("dbusername"),
		    properties.getProperty("dbpassword"));

	    String call = "{call reg_user(?, ?, ? ,? ,?,?,?,?,?,?,?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setQueryTimeout(1800);
	    cstmt.setString(1, fname);
	    cstmt.setString(2, lname);
	    cstmt.setString(3, phone);
	    cstmt.setString(4, email);
	    cstmt.setString(5, street);
	    cstmt.setString(6, state);
	    cstmt.setString(7, zipcode);
	    cstmt.setString(8, idtype);
	    cstmt.setString(9, idnumber);
	    cstmt.setString(10, authenticationManager.passwordEncryptor(password));

	    cstmt.registerOutParameter(11, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(11);

	    while (rs.next()) {
		reguserDetails.setEmail(rs.getString("C_EMAIL"));
		String cusEmail = reguserDetails.getEmail();

		EmailUtility emailUtil = new EmailUtility();

		String mailTxt = "you are registered";
		String mailSubject = "Reg_success ";

		emailUtil.sendEmail(cusEmail, mailSubject, mailTxt);

	    }
	    con.close();

	}

	catch (Exception e) {
	    System.out.println(e);

	}
	return reguserDetails;
    }

    @Override
    @Transactional
    public List<AuthorDetails> getAuthors(String eventId) {
	List<AuthorDetails> authorDetailsret = new ArrayList<AuthorDetails>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"),
		    properties.getProperty("dbusername"), properties.getProperty("dbpassword"));

	    String call = "{call GET_AUTHORS(?,?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, eventId);
	    cstmt.registerOutParameter(2, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(2);
	    while (rs.next()) {
		AuthorDetails authorDetailsTmp = new AuthorDetails();
		authorDetailsTmp.setAuthorId(rs.getString("AUTHOR_ID"));
		authorDetailsTmp.setAuthorName(rs.getString("AUTHOR_NAME"));
		authorDetailsTmp.setAuthorPh(rs.getString("AUTHOR_PH"));
		authorDetailsTmp.setAuthorEmail(rs.getString("EMAIL"));
		authorDetailsTmp.setAuthorAddress(rs.getString("AUTHOR_ADDR"));
		authorDetailsret.add(authorDetailsTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return authorDetailsret;
    }

    @Override
    @Transactional
    public boolean sendSeminarInvitation(String eventId, String authorId) {

	try {
	    SeminarInvitationDetails seminarInvitationDetailsTmp = new SeminarInvitationDetails();
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"),
		    properties.getProperty("dbusername"), properties.getProperty("dbpassword"));

	    String call = "{call SEMINAR_INVITATION(?, ?,?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.setString(1, eventId);
	    cstmt.setString(2, authorId);
	    cstmt.registerOutParameter(3, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(3);
	    while (rs.next()) {

		seminarInvitationDetailsTmp.setAuthorId(rs.getString("AUTHOR_ID"));
		seminarInvitationDetailsTmp.setAuthName(rs.getString("AUTH_NAME"));
		seminarInvitationDetailsTmp.setaPhNo(rs.getString("A_PH_NO"));
		seminarInvitationDetailsTmp.setaEmail(rs.getString("A_EMAIL"));
		seminarInvitationDetailsTmp.setInvitationId(rs.getString("INVITATION_ID"));
		seminarInvitationDetailsTmp.setEventId(rs.getString("event_id"));
		seminarInvitationDetailsTmp.setEventName(rs.getString("event_name"));
		seminarInvitationDetailsTmp.setEventStart(rs.getString("event_start"));
		seminarInvitationDetailsTmp.setEventEnd(rs.getString("event_stop"));

	    }
	    EmailUtility emailUtil = new EmailUtility();
	    String mailTxt = "Your Seminar Invitation Details. " + "\t\n " + seminarInvitationDetailsTmp.toString();
	    String mailSubject = "Seminar Invitation";
	    String authorEmail = seminarInvitationDetailsTmp.getaEmail();
	    emailUtil.sendEmail(authorEmail, mailSubject, mailTxt);

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return true;
    }

    @Override
    @Transactional
    public List<PopularBooks> getPopularBooks() {
	List<PopularBooks> popularBooksret = new ArrayList<PopularBooks>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"),
		    properties.getProperty("dbusername"), properties.getProperty("dbpassword"));

	    String call = "{call get_popular_books(?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(1);
	    while (rs.next()) {
		PopularBooks popularBooksTmp = new PopularBooks();
		popularBooksTmp.setBookName(rs.getString("BOOK_NAME"));
		popularBooksTmp.setNoOfRentals(rs.getString("NO_OF_RENTALS"));
		popularBooksret.add(popularBooksTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return popularBooksret;
    }

    @Override
    @Transactional
    public List<PopularBookGenre> getPopularBookGenre() {
	List<PopularBookGenre> popularBookGenreret = new ArrayList<PopularBookGenre>();
	try {
	    Class.forName(properties.getProperty("class.forname"));
	    Connection con = DriverManager.getConnection(properties.getProperty("dburl"),
		    properties.getProperty("dbusername"), properties.getProperty("dbpassword"));

	    String call = "{call get_popular_bookgenre(?) }";
	    CallableStatement cstmt = con.prepareCall(call);
	    cstmt.registerOutParameter(1, OracleTypes.CURSOR);
	    cstmt.executeUpdate();
	    ResultSet rs = (ResultSet) cstmt.getObject(1);
	    while (rs.next()) {
		PopularBookGenre popularBookGenreTmp = new PopularBookGenre();
		popularBookGenreTmp.setGenre(rs.getString("Genre"));
		popularBookGenreTmp.setNoOfRentals(rs.getString("NO_OF_RENTALS"));
		popularBookGenreret.add(popularBookGenreTmp);
	    }

	    con.close();
	} catch (Exception e) {
	    System.out.println(e);
	}

	return popularBookGenreret;
    }
    

}
