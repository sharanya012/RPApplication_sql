package com.researchpaper.RPApplication.dto;

public class AuthorDTO {
    private int position;
    private String fullName;
    private String department;
    private String organization;
    private String cityCountry;
    private String contact;

    // Getters and Setters
    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getOrganization() {
        return organization;
    }
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCityCountry() {
        return cityCountry;
    }
    public void setCityCountry(String cityCountry) {
        this.cityCountry = cityCountry;
    }

    public String getContact() {
        return contact;
    }
    public void setContact(String contact) {
        this.contact = contact;
    }
}
