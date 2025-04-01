package controllers;

import java.time.LocalDateTime;

public class Booking {
    private int bookingId;
    private Flight flightID;
    private Passenger passengerID;
    private LocalDateTime BookingDateTime;

    public Booking(int bookingId, Flight flight, Passenger passenger) {
        this.bookingId = bookingId;
        this.flightID = flight;
        this.passengerID = passenger;
        this.BookingDateTime = LocalDateTime.now();
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public Flight getFlight() { return flightID; }
    public Passenger getPassenger() { return passengerID; }
    public LocalDateTime getBookingTime() { return BookingDateTime; }
}