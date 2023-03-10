package com.realpubliclibrary.model;

public class SeminarInvitationDetails {
    @Override
    public String toString() {
	return "Invitation ID=" + invitationId + ", Evvent ID=" + eventId + ", Event Name="
		+ eventName + ", Start=" + eventStart + ", End=" + eventEnd;
    }

    private String invitationId;
    private String eventId;
    private String eventName;
    private String eventStart;
    private String eventEnd;
    private String authorId;
    private String authName;
    private String aEmail;
    private String aPhNo;

    public String getInvitationId() {
	return invitationId;
    }

    public void setInvitationId(String invitationId) {
	this.invitationId = invitationId;
    }

    public String getEventId() {
	return eventId;
    }

    public void setEventId(String eventId) {
	this.eventId = eventId;
    }

    public String getEventName() {
	return eventName;
    }

    public void setEventName(String eventName) {
	this.eventName = eventName;
    }

    public String getEventStart() {
	return eventStart;
    }

    public void setEventStart(String eventStart) {
	this.eventStart = eventStart;
    }

    public String getEventEnd() {
	return eventEnd;
    }

    public void setEventEnd(String eventEnd) {
	this.eventEnd = eventEnd;
    }

    public String getAuthorId() {
	return authorId;
    }

    public void setAuthorId(String authorId) {
	this.authorId = authorId;
    }

    public String getAuthName() {
	return authName;
    }

    public void setAuthName(String authName) {
	this.authName = authName;
    }

    public String getaEmail() {
	return aEmail;
    }

    public void setaEmail(String aEmail) {
	this.aEmail = aEmail;
    }

    public String getaPhNo() {
	return aPhNo;
    }

    public void setaPhNo(String aPhNo) {
	this.aPhNo = aPhNo;
    }

}
