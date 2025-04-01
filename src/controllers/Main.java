package controllers;

import java.time.LocalDateTime;
import java.util.List;

import models.FlightBookingSystem;

public class Main {
    public static void main(String[] args) {
        try {
            FlightBookingSystem bookingSystem = new FlightBookingSystem();

            // Add sample flights
            Flight flight1 = new Flight("FL001", "New York", "London", 
                LocalDateTime.of(2023, 6, 1, 10, 0), 
                LocalDateTime.of(2023, 6, 1, 22, 0), 200);
            Flight flight2 = new Flight("FL002", "London", "Paris", 
                LocalDateTime.of(2023, 6, 2, 14, 0), 
                LocalDateTime.of(2023, 6, 2, 16, 0), 150);
            // Search for flights
            List<Flight> availableFlights = bookingSystem.searchFlights("New York", "London", 
                LocalDateTime.of(2023, 6, 1, 0, 0));
            System.out.println("Available flights: " + (availableFlights != null ? availableFlights.size() : 0));

            if (availableFlights != null && !availableFlights.isEmpty()) {
                // Create a booking
                Passenger passenger = new Passenger("John Doe", "P123456", "+1234567890");
                Booking booking = bookingSystem.createBooking(availableFlights.get(0).getFlightNumber(), passenger);
                if (booking != null) {
                    System.out.println("Booking created with ID: " + booking.getBookingId());

                    // Cancel the booking
                    boolean cancelled = bookingSystem.cancelBooking(booking.getBookingId());
                    System.out.println("Booking cancelled: " + cancelled);
                } else {
                    System.out.println("Failed to create booking.");
                }
            } else {
                System.out.println("No flights available for booking.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}