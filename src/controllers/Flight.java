	package controllers;

import java.time.LocalDateTime;

public class Flight {
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private int capacity;
    private int bookedSeats;

    public Flight(String flightNumber, String origin, String destination, 
                  LocalDateTime departureTime, LocalDateTime arrivalTime, int capacity) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.capacity = capacity;
        this.bookedSeats = 0;
    }

    public boolean hasAvailableSeats() {
        return bookedSeats < capacity;
    }

    public void bookSeat() {
        if (hasAvailableSeats()) {
            bookedSeats++;
        } else {
            throw new RuntimeException("No available seats on this flight.");
        }
    }

    public void cancelSeat() {
        if (bookedSeats > 0) {
            bookedSeats--;
        }
    }

    // Getters and setters
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }

    public int getAvailableSeats() { return capacity - bookedSeats; }
}