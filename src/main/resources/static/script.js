var btnLogin = document.getElementById('do-login');
var btnReg = document.getElementById('do-reg');
var btnReguser = document.getElementById('do-reguser');
var btnfgtpwd = document.getElementById('fgt-pwd');
var btnUpdatePwd = document.getElementById('pwd-reset');

window.onload = function() {
	var userType = localStorage.getItem('userType');
	if (userType === 'NA' && document.location.pathname != "/login.html" && document.location.pathname != "/reg.html" && document.location.pathname !="/fgtpwd.html") {
		document.location = "login.html";
	} else {
		if (userType === 'CUS') {
			var cusId = localStorage.getItem('cusId');
			getRentalDetails(cusId);
			getBookedRoomDetails(cusId);
		}
		if (userType === 'EMP') {
			getCustomers('ROOM');
			getCustomers('BOOK');
			const roomavailabilitydiv = document.getElementById('roomavailabilitydiv');
			const roomSeachdiv = document.getElementById('roomSeachdiv');
			roomavailabilitydiv.style.display = 'none';

			roomSeachdiv.style.display = 'none';
		}
	}
};

function getCustomers(tab) {
	var getCustomersurl = "http://localhost:8080/user/getcustomers";
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getCustomersurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var customerdetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table id="getCustomers" class="myTable">
						  <tr class="header">
										<th>First Name</th>
										<th>Last Name</th>
										<th>Phone Number</th>
										<th>Email</th>
										<th></th>
							</tr>`;
			var main = "";
			for (i = 0; i < customerdetails.length; i++) {
				main += "<tr><td>" + customerdetails[i].firstName +
					"</td><td>" + customerdetails[i].lastName +
					"</td><td>" + customerdetails[i].phoneNo +
					"</td><td>" + customerdetails[i].email;
				if (tab === 'BOOK') {
					if (customerdetails[i].hasBookReserved === "Y") {
						main += "</td><td id='" + customerdetails[i].cusId +
							"' onclick='showRentalDetails(" + '"' + customerdetails[i].cusId + '"' +
							")'><button class='primary-button' >Show Rentals</button>";
					}
				}
				if (tab === 'ROOM') {
					if (customerdetails[i].hasRoomReserved === "Y") {
						main += "</td><td id='" + customerdetails[i].cusId +
							"' onclick='getBookedRoomDetails(" + '"' + customerdetails[i].cusId + '"' +
							")'><button class='primary-button' >Booked Rooms</button>";
					}
				}

				main += "</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			if (tab === 'BOOK') {
				document.getElementById("customerdetails").innerHTML = tbl;
				document.getElementById("customerdetails").style.display = "block";
			}
			if (tab === 'ROOM') {
				document.getElementById("customerdetails2").innerHTML = tbl;
				document.getElementById("customerdetails2").style.display = "block";
				document.getElementById("roomCombinedDetails").style.display = "none";

			}
			document.getElementById("rentaldetails").style.display = "none";

		}
	};
	xmlhttp.send();
};


function showRentalDetails(cusId) {
	var getRentalDetailsurl = "http://localhost:8080/user/getrentaldetails?cusId=" + cusId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getRentalDetailsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var rentalDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<div align="center" style="padding:12px" class="primary-button">
							<button class="tablinks" type="button" id="getcustomerdetails" onclick="getCustomers('BOOK')">Reservation History</button>
						</div>
						<table id="rentalDetailstbl" class="myTable">
								  <tr class="header">
												<th>Book Name</th>
												<th>Borrow Date</th>
												<th>Expected Return Date</th>
									<th></th>
									</tr>`;
			var main = "";
			for (i = 0; i < rentalDetails.length; i++) {
				if(rentalDetails[i].isReturned==="N"){
				main += "<tr><td>" + "<label>" + rentalDetails[i].bookName + "</label>" +
					"</td><td>" + "<label>" + rentalDetails[i].borrowedDate + "</label>" +
					"</td><td>" + "<label>" + rentalDetails[i].expReturnDate + "</label>" +
					"</td><td id='" + rentalDetails[i].cusId +
					"' onclick='returnBook(" + '"' + rentalDetails[i].rentalId + '", ' + '"' + cusId + '"' +
					")'><div class='primary-button'><button class='tablinks' >Return Book</button><div>" +
					"</td></tr>";
			}}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("rentaldetails").innerHTML = tbl;
			document.getElementById("rentaldetails").style.display = "block";
			document.getElementById("customerdetails").style.display = "none";

			/*document.getElementById("rentaldetails").style.display = "none";
			var popup = window.open("", "", "width=640,height=480,resizeable,scrollbars"),
				table = document.getElementById("rentalDetailstbl");
			popup.document.write(table.outerHTML);
			popup.document.close();
			if (window.focus)
				popup.focus();*/
		}
	};
	xmlhttp.send();
};

function returnBook(rentalId, cusId) {

	var returnBookurl = "http://localhost:8080/user/returnbook?rentalId=" + rentalId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", returnBookurl, true);
	xmlhttp.onreadystatechange = function() {
		var isCancelled = xmlhttp.responseText;
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			showRentalDetails(cusId);
			if (isCancelled === "true") {
				alert("The Book Has been returned");
			} else {
				alert("The Book return has been interrupted. Please try again.");
			}
		}
	};
	xmlhttp.send();
}

function getRentalDetails(cusId) {
	var getRentalDetailsurl = "http://localhost:8080/user/getrentaldetails?cusId=" + cusId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getRentalDetailsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var rentalDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table id="getRentalDetailstbl" class="myTable">
							  <tr class="header">
											<th>Book Name</th>
											<th>Borrow Date</th>
											<th>Expected Return Date</th>
				
								</tr>`;
			var main = "";
			for (i = 0; i < rentalDetails.length; i++) {
				main += "<tr><td>" + "<label>" + rentalDetails[i].bookName + "</label>" +
					"</td><td>" + "<label>" + rentalDetails[i].borrowedDate + "</label>" +
					"</td><td>" + "<label>" + rentalDetails[i].expReturnDate + "</label>" +
					"</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("rentaldetails").innerHTML = tbl;
		}
	};
	xmlhttp.send();
};

function getBookedRoomDetails(cusId) {
	var userType = localStorage.getItem('userType');
	var empAdditionalButton = `&nbsp;`
	if (userType === 'EMP') {

		var empAdditionalButton = `<div align="center" style="padding:12px" class="primary-button">
							<button class="tablinks" type="button" id="getcustomerdetails" onclick="getCustomers('ROOM')">Reservation History</button>
							</div>`;
	}
	if (userType === 'CUS') {
		cusId = localStorage.getItem('cusId');
	}
	var getBookedRoomDetailsurl = "http://localhost:8080/user/bookedroomdetails?cusId=" + cusId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getBookedRoomDetailsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var bookedRoomDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table id="getBookedRoomDetailstbl" class="myTable">
							  <tr class="header">
											<th>Reservation Date</th>
											<th>Reservation Slot</th>
											<th>Room Number</th>
											<th></th>
										</tr>`;
			var main = "";
			for (i = 0; i < bookedRoomDetails.length; i++) {
				var slot = "";
				if (bookedRoomDetails[i].slots[0] === 'S1') {
					slot = '9:00 AM - 11:00 AM';
				}
				if (bookedRoomDetails[i].slots[0] === 'S2') {
					slot = '11:00 AM - 1:00 PM';
				}
				if (bookedRoomDetails[i].slots[0] === 'S3') {
					slot = '1:00 PM - 3:00 PM';
				}
				if (bookedRoomDetails[i].slots[0] === 'S4') {
					slot = '3:00 PM - 5:00 PM';
				}
				main += "<tr><td>" + "<label>" + bookedRoomDetails[i].reservationDate + "</label>" +
					"</td><td>" + "<label>" + slot + "</label>" +
					"</td><td>" + "<label>" + bookedRoomDetails[i].roomId + "</label>" +
					"</td><td onclick='cancelRoomReservation(" + '"' + bookedRoomDetails[i].reservationId + '"' + ', "' + cusId + '"' +
					")'><button class='primary-button' >Cancel Reservation</button>" +
					"</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = empAdditionalButton + tbltop + main + tblbottom;
			document.getElementById("roomCombinedDetails").innerHTML = tbl;

			document.getElementById("roomCombinedDetails").style.display = "block";
			document.getElementById("customerdetails2").style.display = "none";
		}
	};
	xmlhttp.send();
};

function cancelRoomReservation(reservationId, cusId) {
	var cancelRoomReservationurl = "http://localhost:8080/user/cancelroomreservation?reservationId=" + reservationId + "&cusId=" + cusId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", cancelRoomReservationurl, true);
	xmlhttp.onreadystatechange = function() {
		var isCancelled = xmlhttp.responseText;
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			getBookedRoomDetails(cusId);
			if (isCancelled === "true") {
				alert("The Room reservation has been Cancelled");
			} else {
				alert("The Reservation cancellation has been interrupted. Please try again.");
			}
		}
	};
	xmlhttp.send();


};

function getRoomAvailability() {
	var e1 = document.getElementById("reservationDate");
	var selectedDate = e1.value;
	var e2 = document.getElementById("numOfPeople");
	var numOfPeople = e2.value;
	var cusId = localStorage.getItem('cusId');
	if (selectedDate === "" || numOfPeople === "") {
		alert('Date and Number is mandatory');
	} else {
		var getRoomAvailabilityurl = "http://localhost:8080/user/getroomavailability?selectedDate=" + selectedDate + "&numOfPeople=" + numOfPeople;
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.open("GET", getRoomAvailabilityurl, true);
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
				var roomAvailabilityDetails = JSON.parse(xmlhttp.responseText);
				var tbltop = `<table id="getRoomAvailability" class="myTable">
								  <tr class="header">
													<th>Room Name</th>
													<th>Max Capacity</th>													
													<th>Slots</th>
													<th></th>
												</tr>`;
				var main = "";
				for (i = 0; i < roomAvailabilityDetails.length; i++) {
					var availableSlotsDDLStart = "<select id='availableSlots_" + roomAvailabilityDetails[i].roomId + "'>";
					var availableSlotsDDLEnd = "</select>";
					var availableSlotsDDL = "";
					for (let j = 0; j < roomAvailabilityDetails[i].slots.length; j++) {
						var slot = "";
						if (roomAvailabilityDetails[i].slots[j] === 'S1') {
							slot = '9:00 AM - 11:00 AM';
						}
						if (roomAvailabilityDetails[i].slots[j] === 'S2') {
							slot = '11:00 AM - 1:00 PM';
						}
						if (roomAvailabilityDetails[i].slots[j] === 'S3') {
							slot = '1:00 PM - 3:00 PM';
						}
						if (roomAvailabilityDetails[i].slots[j] === 'S4') {
							slot = '3:00 PM - 5:00 PM';
						}
						availableSlotsDDL += '<option value="' + roomAvailabilityDetails[i].slots[j] + '">' + slot + '</option>';

					}
					main += "<tr><td>" + "<label>" + roomAvailabilityDetails[i].roomId + "</label>" +
						"</td><td>" + "<label>" + roomAvailabilityDetails[i].capacity + "</label>" +
						"</td><td>" + availableSlotsDDLStart + availableSlotsDDL + availableSlotsDDLEnd +
						"</td><td onclick='bookRoom(" + '"' + cusId + '"' + ", " + '"' + roomAvailabilityDetails[i].roomId + '"' +
						")'><button class='primary-button' >Book</button>" +
						"</td></tr>";
				}
				var tblbottom = "</table>";
				var tbl = tbltop + main + tblbottom;
				//document.getElementById("roomAvailabilityDetails").innerHTML = tbl;
				document.getElementById("roomCombinedDetails").innerHTML = tbl;

			}
		};
		xmlhttp.send();
	}
};

if (btnLogin != null) {
	btnLogin.onclick = function() {
		var email = document.getElementById('email').value;
		var password = document.getElementById('password').value;
		if (validateEmail(email) === true) {
			if(validatePassword(password)===true){
			validateUser(email, password);
			}
			else{
				alert("Invalid Password!\nPassowrd must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters")
			}
		} else {
			alert("You have entered an invalid email address!")
		}
	}
};

if (btnfgtpwd != null) {
	btnfgtpwd.onclick = function() {
		var email = document.getElementById('email').value;
		if (validateEmail(email) === true) {
			verify_email(email)
		} else {
			alert("You have entered an invalid email address!")
		}
	}
};
if (btnUpdatePwd != null) {
	var email = document.getElementById('fgt_newpwd').value;
	btnUpdatePwd.onclick = function() {
			if(validatePassword(password)===true){
			pwd_reset();
			}
			else{
				alert("Invalid Password!\nPassowrd must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters")
			}
		

	}
};

if (btnReg != null) {
	btnReg.onclick = function() {
		regform();

	}
};
if (btnReguser != null) {
	btnReguser.onclick = function() {
		var email = document.getElementById("reg_email").value;
		var password = document.getElementById("reg_password").value;
		if (validateEmail(email) === true) {
			if(validatePassword(password)===true){
			reguser();
			}
			else{
				alert("Invalid Password!\nPassowrd must contain at least one number and one uppercase and lowercase letter, and at least 8 or more characters")
			}
		} else {
			alert("You have entered an invalid email address!")
		}
		

	}
};
function regform() {
	document.location = "reg.html";
};

function reguser() {

	var e1 = document.getElementById("reg_fname");
	var fname = e1.value;
	var e2 = document.getElementById("reg_lname");
	var lname = e2.value;
	var e1 = document.getElementById("reg_phone");
	var phone = e1.value;
	var e2 = document.getElementById("reg_email");
	var email = e2.value;
	var e1 = document.getElementById("reg_street");
	var street = e1.value;
	var e2 = document.getElementById("reg_state");
	var state = e2.value;
	var e1 = document.getElementById("reg_zipcode");
	var zipcode = e1.value;
	var e2 = document.getElementById("reg_idtype");
	var idtype = e2.value;
	var e1 = document.getElementById("reg_idnumber");
	var idnumber = e1.value;
	var e2 = document.getElementById("reg_password");
	var password = e2.value;

	var reguserurl = "http://localhost:8080/login/reguser?fname=" + fname + "&lname=" + lname + "&phone=" + phone + "&email=" + email + "&street=" + street + "&state=" + state + "&zipcode=" + zipcode + "&idtype=" + idtype + "&idnumber=" + idnumber + "&password=" + password;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", reguserurl, true);
	xmlhttp.onreadystatechange = function() {
		var userDetails = JSON.parse(xmlhttp.responseText);
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			document.location = "login.html";

		}
	};
	xmlhttp.send();
};



function bookRoom(cusId, roomId) {
	var slot = document.getElementById('availableSlots_' + roomId).value;
	var selectedDate = document.getElementById("reservationDate").value;
	var bookRoomurl = "http://localhost:8080/user/bookroom?cusId=" + cusId + "&roomId=" + roomId + "&slot=" + slot + "&selectedDate=" + selectedDate;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", bookRoomurl, true);
	xmlhttp.onreadystatechange = function() {
		var isBooked = xmlhttp.responseText;
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			getRoomAvailability();
			if (isBooked === "false") {
				alert("The Room has been booked by other Member");
			} else {
				alert("The Room has been booked. Please check your mail");
			}
		}
	};
	xmlhttp.send();

};

function validateUser(email, password) {
	var validateuserurl = "http://localhost:8080/login/validateuser?emailId=" + email + "&password=" + password;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", validateuserurl, true);
	xmlhttp.onreadystatechange = function() {
		var userDetails = JSON.parse(xmlhttp.responseText);
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			if (userDetails.userId === "0") {
				alert("Invalid Login");
			} else {
				localStorage.setItem('empId', userDetails.empId);
				localStorage.setItem('cusId', userDetails.cusId);
				localStorage.setItem('userType', userDetails.userType);
				document.location = "index.html";
			}
		}
	};
	xmlhttp.send();
};

function verify_email(email) {
	var verifyuserurl = "http://localhost:8080/login/verifyemail?email=" + email;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", verifyuserurl, true);
	xmlhttp.onreadystatechange = function() {
		var fgtpwddetails = JSON.parse(xmlhttp.responseText);
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			if (fgtpwddetails.userOtp === 'null') {
				alert("No User Exists");
			} else {
				localStorage.setItem('userEmail', fgtpwddetails.email)
				alert("OTP Sent to your email.");
				document.location = "fgtpwd.html";

			}
		}
	};
	xmlhttp.send();
};

function pwd_reset() {

	var e1 = document.getElementById("fgt_otp");
	var inputotp = e1.value;
	var e2 = document.getElementById("fgt_newpwd");
	var newpwd = e2.value;
	var useremail = localStorage.getItem('userEmail');


	var pwdreseturl = "http://localhost:8080/login/pwdreset?inputotp=" + inputotp + "&newpwd=" + newpwd + "&useremail=" + useremail;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", pwdreseturl, true);
	xmlhttp.onreadystatechange = function() {
		var postresetDetails = JSON.parse(xmlhttp.responseText);
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			alert('password successfully updated,login back')
			document.location = "login.html";
		}
		else {
			document.location = "fgtpwd.html";
		}
	};
	xmlhttp.send();
};


/*
 ** validate email
 */
function validateEmail(mail) {
	if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(mail)) {
		return true;
	}
	return false;
};

/*
 ** validate email
 */
function validatePassword(password) {
	if (/(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}/.test(password)) {
		return true;
	}
	return false;
};

function getFutureEvents() {
	var userType = localStorage.getItem('userType');
	var getExhibitionsurl = "http://localhost:8080/user/getevents?userType=" + userType;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getExhibitionsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var getExhibitionDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = "";
			if (userType === "CUS") {
				tbltop = `<table class="myTable">
						  <tr class="header">
						    <th>Event Name</th>
							<th>Event Type</th>
							<th>Genre</th>
						    <th>Start Date</th>
							<th>End Date</th>
							<th></th>
						  </tr>`;
			} else {
				tbltop = `<table class="myTable">
							  <tr class="header">
							    <th>Event Name</th>
								<th>Event Type</th>
								<th>Genre</th>
							    <th>Start Date</th>
								<th>End Date</th>
								<th>Expense</th>
								<th></th>
							  </tr>`;
			}

			var main = "";
			for (i = 0; i < getExhibitionDetails.length; i++) {
				main += "<tr><td>" + "<label>" + getExhibitionDetails[i].eventName + "</label>";
				if (getExhibitionDetails[i].eventType === "E") {
					main += "</td><td>" + "<label>Exhibition </label>";
				} else {
					main += "</td><td>" + "<label> Seminar </label>";
				}
				main += "</td><td>" + "<label>" + getExhibitionDetails[i].genre + "</label>" +
					"</td><td>" + "<label>" + getExhibitionDetails[i].startDate + "</label>" +
					"</td><td>" + "<label>" + getExhibitionDetails[i].endDate + "</label>";
				if (userType === "CUS") {
					main += "</td><td onclick='bookExhibition(" + '"' + getExhibitionDetails[i].eventId + '"' +
						")'><button class='primary-button' >Book</button>";
				} else {
					if (getExhibitionDetails[i].eventType === "S") {
						main += "</td><td onclick='getSeminarSponsors(" + '"' + getExhibitionDetails[i].eventId + '"' +
							")'><button class='primary-button' >Get Sponsors</button>";
						main += "</td><td onclick='getAuthors(" + '"' + getExhibitionDetails[i].eventId + '"' +
							")'><button class='primary-button' >Send Invitation</button>";
					} else {
						main += "</td><td>" + "<label>" + getExhibitionDetails[i].exhibitionExpense + "</label>";
					}

				}
				main += "</td></tr>";
			}
		}
		var tblbottom = "</table>";
		var tbl = tbltop + main + tblbottom;
		document.getElementById("getExhibitionCombinedDetails").innerHTML = tbl;
		document.getElementById("getExhibitionCombinedDetails").style.display = "block";
		document.getElementById("seminarsponsorsdetails").style.display = "none";
		document.getElementById("authordetails").style.display = "none";
	}
	xmlhttp.send();
};

function getSeminarSponsors(eventId) {
	var getseminarsponsorsurl = "http://localhost:8080/user/getseminarsponsors?eventId=" + eventId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getseminarsponsorsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var seminarsponsorsDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table class="myTable">
							  <tr class="header">
											<th>Sponsor Name</th>
											<th>Sponsor Type</th>
											<th>Amount</th>
				
								</tr>`;
			var main = "";
			for (i = 0; i < seminarsponsorsDetails.length; i++) {
				main += "<tr><td>" + "<label>" + seminarsponsorsDetails[i].sponsorName + "</label>" +
					"</td><td>" + "<label>" + seminarsponsorsDetails[i].sponsorType + "</label>" +
					"</td><td>" + "<label>" + seminarsponsorsDetails[i].sponsorAmount + "</label>" +
					"</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("seminarsponsorsdetails").innerHTML = tbl;

			document.getElementById("seminarsponsorsdetails").style.display = "block";
			document.getElementById("getExhibitionCombinedDetails").style.display = "none";
		}
	};
	xmlhttp.send();
}

function getAuthors(eventId) {
	var getauthorsurl = "http://localhost:8080/user/getauthors?eventId=" + eventId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getauthorsurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var authorsDetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table class="myTable">
							  <tr class="header">
											<th>Author Name</th>
											<th>Phone Number</th>
											<th>Email</th>
											<th>Address</th>
											<th></th>
				
								</tr>`;
			var main = "";
			for (i = 0; i < authorsDetails.length; i++) {
				main += "<tr><td>" + "<label>" + authorsDetails[i].authorName + "</label>" +
					"</td><td>" + "<label>" + authorsDetails[i].authorPh + "</label>" +
					"</td><td>" + "<label>" + authorsDetails[i].authorEmail + "</label>" +
					"</td><td>" + "<label>" + authorsDetails[i].authorAddress + "</label>" +
					"</td><td onclick='sendSeminarInvitation(" + '"' + authorsDetails[i].authorId + '"' +
							', "' + eventId + '"' + 
							")'><button class='primary-button' >Send Invite</button>" +
					"</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("authordetails").innerHTML = tbl;

			document.getElementById("authordetails").style.display = "block";

			document.getElementById("seminarsponsorsdetails").style.display = "none";

			document.getElementById("getExhibitionCombinedDetails").style.display = "none";
		}
	};
	xmlhttp.send();
}

function sendSeminarInvitation(authorId, eventId) {
	var sendseminarinvitationurl = "http://localhost:8080/user/sendseminarinvitation?eventId=" + eventId + "&authorId=" + authorId;

	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("POST", sendseminarinvitationurl, true);
	xmlhttp.onreadystatechange = function() {
		getAuthors(eventId)
		var sent = xmlhttp.responseText;
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			if (sent === "false") {
				alert("The Invitation was not sent");
			} else {
				alert("The Invitation is sent. Please check your mail");
			}
		}
	};
	xmlhttp.send();
}

function bookExhibition(exhibitionId) {
	var cusId = localStorage.getItem('cusId');
	var bookExhibitionurl = "http://localhost:8080/user/bookexhibition?cusId=" + cusId + "&exhibitionId=" + exhibitionId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", bookExhibitionurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var isBooked = xmlhttp.responseText;
			if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
				if (isBooked === "false") {
					alert("An internal error has occured. Please try again.");
				} else {
					alert("The Exhibition has been booked. Please check your mail");
				}
			}
		}
	}
	xmlhttp.send();
};

function getCustExhibition() {
	if (localStorage.getItem('userType') === 'EMP') {
		var cusId = localStorage.getItem('empId');
	} else {
		var cusId = localStorage.getItem('cusId');
	}
	var getcustexhibitionurl = "http://localhost:8080/user/getcustexhibition?cusId=" + cusId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getcustexhibitionurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var custexhibitiondetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table class="myTable">
							  <tr class="header">
							    <th>Event Name</th>
								<th>Genre</th>
							    <th>Start Date</th>
								<th>End Date</th>
								<th></th>
								
							  </tr>`;
			var main = "";
			for (i = 0; i < custexhibitiondetails.length; i++) {
				main += "<tr><td>" + "<label>" + custexhibitiondetails[i].eventName + "</label>" +
					"</td><td>" + "<label>" + custexhibitiondetails[i].genre + "</label>" +
					"</td><td>" + "<label>" + custexhibitiondetails[i].startDate + "</label>" +
					"</td><td>" + "<label>" + custexhibitiondetails[i].endDate + "</label>";
				if (custexhibitiondetails[i].isFutureEvent === 'Y') {
					main += "</td><td onclick='cancelCustExhBooking(" + '"' + custexhibitiondetails[i].eventId + '"' + ', "' + cusId + '"' +
						")'><button class='primary-button'>Cancel</button>";
				}
				main += "</td></tr>";
			}
			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("getExhibitionCombinedDetails").innerHTML = tbl;
			document.getElementById("getExhibitionCombinedDetails").style.display = "block";
			document.getElementById("seminarsponsorsdetails").style.display = "none";
			document.getElementById("authordetails").style.display = "none";
		}
	}
	xmlhttp.send();
};

function cancelCustExhBooking(eventId, cusId) {

	var cusId = localStorage.getItem('cusId');
	var bookExhibitionurl = "http://localhost:8080/user/cancelcustexhbooking?cusId=" + cusId + "&eventId=" + eventId;
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", bookExhibitionurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			getCustExhibition();
			var isBooked = xmlhttp.responseText;
			if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
				if (isBooked === "false") {
					alert("An internal error has occured. Please try again.");
				} else {
					alert("The Exhibition has been Cancelled. Please check your mail");
				}
			}
		}
	}
	xmlhttp.send();
}

function exit() {
	var cusId = localStorage.getItem('cusId');
	location.replace('/login.html')
	localStorage.clear();
};

function getPopularBooks() {
	var getPopularBooksurl = "http://localhost:8080/user/getPopularBooks";
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getPopularBooksurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
				var popularBooksdetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table class="myTable">
							  <tr class="header">
							    <th>Book Name</th>
								<th>No Of Rentals</th>
								
							  </tr>`;
			var main = "";
			for (i = 0; i < popularBooksdetails.length; i++) {
				main += "<tr><td>" + "<label>" + popularBooksdetails[i].bookName + "</label>" +
					"</td><td>" + "<label>" + popularBooksdetails[i].noOfRentals + "</label>"
			}
			main += "</td></tr>";

			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("myChart").innerHTML = tbl;
		}
	}
	xmlhttp.send();
}
function getPopularBookGenre() {
	var getPopularBookGenreurl = "http://localhost:8080/user/getPopularBookGenre";
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.open("GET", getPopularBookGenreurl, true);
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState === 4 && xmlhttp.status === 200) {
			var PopularBookGenredetails = JSON.parse(xmlhttp.responseText);
			var tbltop = `<table class="myTable">
							  <tr class="header">
							    <th>Genre</th>
								<th>No Of Rentals</th>
								
							  </tr>`;
			var main = "";
			for (i = 0; i < PopularBookGenredetails.length; i++) {
				main += "<tr><td>" + "<label>" + PopularBookGenredetails[i].genre + "</label>" +
					"</td><td>" + "<label>" + PopularBookGenredetails[i].noOfRentals + "</label>"
			}
			main += "</td></tr>";

			var tblbottom = "</table>";
			var tbl = tbltop + main + tblbottom;
			document.getElementById("myChart").innerHTML = tbl;
		}
	}



	xmlhttp.send();
}
