package controllers;

public class Passenger {
    private String name;
    private String passportNumber;
    private String contactNumber;

    public Passenger(String name, String passportNumber, String contactNumber) {
        this.name = name;
        this.passportNumber = passportNumber;
        this.contactNumber = contactNumber;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
}