package com.realpubliclibrary.model;

public class SponsorDetails {

    private String eventId;

    private String sponsorId;

    private String sponsorName;

    private String sponsorType;

    private String sponsorAmount;

    public String getEventId() {
	return eventId;
    }

    public void setEventId(String eventId) {
	this.eventId = eventId;
    }

    public String getSponsorId() {
	return sponsorId;
    }

    public void setSponsorId(String sponsorId) {
	this.sponsorId = sponsorId;
    }

    public String getSponsorName() {
	return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
	this.sponsorName = sponsorName;
    }

    public String getSponsorType() {
	return sponsorType;
    }

    public void setSponsorType(String sponsorType) {
	this.sponsorType = sponsorType;
    }

    public String getSponsorAmount() {
	return sponsorAmount;
    }

    public void setSponsorAmount(String sponsorAmount) {
	this.sponsorAmount = sponsorAmount;
    }

}
