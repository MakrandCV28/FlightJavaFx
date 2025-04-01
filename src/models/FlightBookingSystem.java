package models;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import controllers.Booking;
import controllers.Flight;
import controllers.Passenger;

public class FlightBookingSystem {

	public boolean registerUser(String username, String password, String email) {
	    String sql = "INSERT INTO user_accountMT (username, password_hash, email) VALUES (?, ?, ?)";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, password); // Note: In a real application, you should hash the password
	        pstmt.setString(3, email);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	private boolean authenticate(String username, String password) {
	    String sql = "SELECT * FROM user_accountMT WHERE username = ? AND password = ?";
	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, password); // Note: In a real application, you should hash the password
	        ResultSet rs = pstmt.executeQuery();
	        return rs.next(); // If a row is returned, authentication is successful
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
    public List<Flight> searchFlights(String origin, String destination, LocalDateTime date) {
        List<Flight> availableFlights = new ArrayList<>();
        String sql = "SELECT * FROM flightsMT WHERE origin = ? AND destination = ? AND DATE(departureTime) = ? AND availableseats > 0";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, origin);
            pstmt.setString(2, destination);
            pstmt.setDate(3, java.sql.Date.valueOf(date.toLocalDate()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight(
                        rs.getString("flightnumber"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getTimestamp("departureTime").toLocalDateTime(),
                        rs.getTimestamp("arrivalTime").toLocalDateTime(),
                        rs.getInt("capacity")
                    );
                    flight.setBookedSeats(rs.getInt("capacity") - rs.getInt("availableseats"));
                    availableFlights.add(flight);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableFlights;
    }

    public Booking createBooking(String flightNumber, Passenger passenger) 
     {
        String sqlFlight = "SELECT * FROM flightsMT WHERE flightnumber = ? AND availableseats > 0";
        String sqlBooking = "INSERT INTO bookingsMT ( flightnumber, passengername, passportnumber, contactnumber, BookingDate) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
        String sqlUpdateFlight = "UPDATE flightsMT SET availableseats = availableseats - 1 WHERE flightnumber = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmtFlight = conn.prepareStatement(sqlFlight);
             PreparedStatement pstmtBooking = conn.prepareStatement(sqlBooking, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement pstmtUpdateFlight = conn.prepareStatement(sqlUpdateFlight)) {
            
            conn.setAutoCommit(false);
            
            pstmtFlight.setNString(1, flightNumber);
            ResultSet rsFlight = pstmtFlight.executeQuery();
            
            if (rsFlight.next()) {
                Flight flight = new Flight(
                    rsFlight.getString("flightnumber"),
                    rsFlight.getString("origin"),
                    rsFlight.getString("destination"),
                    rsFlight.getTimestamp("departureTime").toLocalDateTime(),
                    rsFlight.getTimestamp("arrivalTime").toLocalDateTime(),
                    rsFlight.getInt("capacity")
                );
                
                pstmtBooking.setString(1, flightNumber);
                pstmtBooking.setString(2, passenger.getName());
                pstmtBooking.setString(3, passenger.getPassportNumber());
                pstmtBooking.setString(4, passenger.getContactNumber());
                pstmtBooking.executeUpdate();
                
                ResultSet rsBooking = pstmtBooking.getGeneratedKeys();
                if (rsBooking.next()) {
                    int bookingId = rsBooking.getInt(1);
                    
                    pstmtUpdateFlight.setString(1, flightNumber);
                    pstmtUpdateFlight.executeUpdate();
                    
                    conn.commit();
                    return new Booking(bookingId, flight, passenger);
                }
            }
            
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, f.* FROM bookingsMT b JOIN flightsMT f ON b.flightnumber = f.flightnumber WHERE b.passportnumber = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String passportNumber = null;
			pstmt.setString(1, passportNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Flight flight = new Flight(
                        rs.getString("flightnumber"),
                        rs.getString("origin"),
                        rs.getString("destination"),
                        rs.getTimestamp("departureTime").toLocalDateTime(),
                        rs.getTimestamp("arrivalTime").toLocalDateTime(),
                        rs.getInt("capacity")
                    );
                    Passenger passenger = new Passenger(
                        rs.getString("passenger_name"),
                        rs.getString("passport_number"),
                        rs.getString("contact_number")
                    );
                    Booking booking = new Booking(rs.getInt("booking_id"), flight, passenger);
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }


    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE flightsMT SET origin = ?, destination = ?, departureTime = ?, arrivalTime = ?, capacity = ?, availableseats = ? WHERE flightnumber = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flight.getOrigin());
            pstmt.setString(2, flight.getDestination());
            pstmt.setTimestamp(3, Timestamp.valueOf(flight.getDepartureTime()));
            pstmt.setTimestamp(4, Timestamp.valueOf(flight.getArrivalTime()));
            pstmt.setInt(5, flight.getCapacity());
            pstmt.setInt(6, flight.getAvailableSeats());
            pstmt.setString(7, flight.getFlightNumber());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
   
    public boolean cancelBooking(int bookingId) {
        String sqlGetFlight = "SELECT FlightNumber FROM BookingsMT WHERE BookingID = ?";
        String sqlDeleteBooking = "DELETE FROM BookingsMT WHERE BookingID = ?";
        String sqlUpdateFlight = "UPDATE FlightsMT SET AvailableSeats = AvailableSeats + 1 WHERE FlightNumber = ?";
        
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);  // Start transaction
            
            // First, get the flight number for the booking
            try (PreparedStatement pstmt = conn.prepareStatement(sqlGetFlight)) {
                pstmt.setInt(1, bookingId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String flightNumber = rs.getString("FlightNumber");
                    
                    // Delete the booking
                    try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDeleteBooking)) {
                        deleteStmt.setInt(1, bookingId);
                        deleteStmt.executeUpdate();
                    }
                    
                    // Update available seats in the flight
                    try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdateFlight)) {
                        updateStmt.setString(1, flightNumber);
                        updateStmt.executeUpdate();
                    }
                    
                    conn.commit();
                    return true;
                }
                
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteFlight(String flightNumber) {
        String sql = "DELETE FROM flightsMT WHERE flightnumber = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, flightNumber);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifyBooking(int bookingId, String newFlightNumber, Passenger updatedPassenger) {
        String sqlUpdateBooking = "UPDATE bookingsMT SET flightnumber = ?, passengername = ?, passportnumber = ?, contactnumber = ? WHERE bookingid = ?";
        String sqlCheckFlight = "SELECT availableseats FROM flightsMT WHERE flightnumber = ?";
        String sqlUpdateOldFlight = "UPDATE flightsMT SET availableseats = availableseats + 1 WHERE flightnumber = (SELECT flightnumber FROM bookingsMT WHERE bookingid = ?)";
        String sqlUpdateNewFlight = "UPDATE flightsMT SET availableseats = availableseats - 1 WHERE flightnumber = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmtUpdateBooking = conn.prepareStatement(sqlUpdateBooking);
             PreparedStatement pstmtCheckFlight = conn.prepareStatement(sqlCheckFlight);
             PreparedStatement pstmtUpdateOldFlight = conn.prepareStatement(sqlUpdateOldFlight);
             PreparedStatement pstmtUpdateNewFlight = conn.prepareStatement(sqlUpdateNewFlight)) {

            conn.setAutoCommit(false);

            // Check if the new flight has available seats
            pstmtCheckFlight.setString(1, newFlightNumber);
            ResultSet rs = pstmtCheckFlight.executeQuery();
            if (rs.next() && rs.getInt("availableseats") > 0) {
                // Update the booking
                pstmtUpdateBooking.setString(1, newFlightNumber);
                pstmtUpdateBooking.setString(2, updatedPassenger.getName());
                pstmtUpdateBooking.setString(3, updatedPassenger.getPassportNumber());
                pstmtUpdateBooking.setString(4, updatedPassenger.getContactNumber());
                pstmtUpdateBooking.setInt(5, bookingId);
                pstmtUpdateBooking.executeUpdate();

                // Update the old flight's available seats
                pstmtUpdateOldFlight.setInt(1, bookingId);
                pstmtUpdateOldFlight.executeUpdate();

                // Update the new flight's available seats
                pstmtUpdateNewFlight.setString(1, newFlightNumber);
                pstmtUpdateNewFlight.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public List<Booking> getBookingsByPassportandbookingid(String passportNumber, int bookingId) {
	    List<Booking> bookings = new ArrayList<>();
	    String sql = "SELECT b.*, f.* FROM bookingsMT b JOIN flightsMT f ON b.flightnumber = f.flightnumber WHERE b.passportnumber = ? AND b.bookingid = ?";
	    try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, passportNumber);
	        pstmt.setInt(2, bookingId);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                Flight flight = new Flight(
	                    rs.getString("flightnumber"),
	                    rs.getString("origin"),
	                    rs.getString("destination"),
	                    rs.getTimestamp("departureTime").toLocalDateTime(),
	                    rs.getTimestamp("arrivalTime").toLocalDateTime(),
	                    rs.getInt("capacity")
	                );
	                Passenger passenger = new Passenger(
	                    rs.getString("passengername"),
	                    rs.getString("passportnumber"),
	                    rs.getString("contactnumber")
	                );
	                Booking booking = new Booking(rs.getInt("bookingid"), flight, passenger);
	                bookings.add(booking);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return bookings;
	}
	
}