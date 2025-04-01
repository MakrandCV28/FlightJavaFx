package views;

import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.DBUtil;
import models.FlightBookingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import controllers.Booking;
import controllers.Flight;
import controllers.Passenger;

public class FlightBookingSystemApp extends Application {

	private FlightBookingSystem bookingSystem;
	private TextArea resultArea;

	@Override
	public void start(Stage primaryStage) {
		bookingSystem = new FlightBookingSystem();
		primaryStage.setTitle("Flight Booking System");
		primaryStage.setScene(createLoginScene(primaryStage));
		primaryStage.show();

	}

	public void start1(Stage primaryStage) {
		bookingSystem = new FlightBookingSystem();

		TabPane tabPane = new TabPane();

		Tab searchTab = new Tab("Search Flights");
		searchTab.setContent(createSearchFlightsPane());
		searchTab.setClosable(false);

		Tab bookingTab = new Tab("Book Flight");
		bookingTab.setContent(createBookingPane());
		bookingTab.setClosable(false);

		Tab manageTab = new Tab("Manage Bookings");
		manageTab.setContent(createManageBookingsPane());
		manageTab.setClosable(false);
		
		Tab modifyBookingTab = new Tab("Modify Bookings");
		modifyBookingTab.setContent(createModifyBookingPane());
		modifyBookingTab.setClosable(false);

		tabPane.getTabs().addAll(searchTab, bookingTab, manageTab);

		resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setPrefHeight(200);

		VBox root = new VBox(10);
		root.setPadding(new Insets(10));
		root.getChildren().addAll(tabPane, resultArea);

		Scene scene = new Scene(root, 600, 500);
		primaryStage.setTitle("Flight Booking System");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		scene.getStylesheets().add(getClass().getResource("/flightbooking-styles.css").toExternalForm());
	}


	private Scene createLoginScene(Stage primaryStage) {
		GridPane grid = new GridPane();
		grid.setAlignment(javafx.geometry.Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");

		Button loginButton = new Button("Login");
		Button registerButton = new Button("Register");
		
		loginButton.setStyle("-fx-background-color: #ADD8E6;");
	    registerButton.setStyle("-fx-background-color: #ADD8E6;");
		
		grid.add(new Label("Username:"), 0, 0);
		grid.add(usernameField, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(passwordField, 1, 1);
		grid.add(loginButton, 1, 2);
		grid.add(registerButton, 1, 3);

		loginButton.setOnAction(e -> {
			if (authenticate(usernameField.getText(), passwordField.getText())) {
				primaryStage.setScene(createMainScene());
			} else {
				showAlert("Login Failed", "Invalid username or password", Alert.AlertType.ERROR);
			}
		});

		registerButton.setOnAction(e -> {
			showRegistrationDialog(primaryStage);
		});

		return new Scene(grid, 300, 200);
	}

	private boolean authenticate(String username, String password) {
		return DBUtil.authenticateUser(username, password);
	}

	private void showRegistrationDialog(Stage primaryStage) {
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("User Registration");

		ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Username");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");
		TextField email = new TextField();
		email.setPromptText("Email");

		grid.add(new Label("Username:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);
		grid.add(new Label("Email:"), 0, 2);
		grid.add(email, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == registerButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			String usernameStr = usernamePassword.getKey();
			String passwordStr = usernamePassword.getValue();
			String emailStr = email.getText();

			if (bookingSystem.registerUser(usernameStr, passwordStr, emailStr)) {
				showAlert("Registration Successful", "You can now login with your credentials", Alert.AlertType.INFORMATION);
			} else {
				showAlert("Registration Failed", "Please try again", Alert.AlertType.ERROR);
			}
		});
	}

	private void showAlert(String title, String content, Alert.AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private Scene createMainScene() {
		TabPane tabPane = new TabPane();

		Tab searchTab = new Tab("Search Flights");
		searchTab.setContent(createSearchFlightsPane());
		searchTab.setClosable(false);

		Tab bookingTab = new Tab("Book Flight");
		bookingTab.setContent(createBookingPane());
		bookingTab.setClosable(false);

		Tab manageTab = new Tab("Manage Bookings");
		manageTab.setContent(createManageBookingsPane());
		manageTab.setClosable(false);
		
		Tab modifyBookingTab = new Tab("Modify Booking");
		modifyBookingTab.setContent(createModifyBookingPane());
		modifyBookingTab.setClosable(false);

		tabPane.getTabs().addAll(searchTab, bookingTab, manageTab, modifyBookingTab );
		
		searchTab.setStyle("-fx-background-color: #ADD8E6;"); // Light red
	    bookingTab.setStyle("-fx-background-color: #90EE90;"); // Light green
	    manageTab.setStyle("-fx-background-color: #FDFD96;"); // Light blue


		resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setPrefHeight(200);

		Button logoutButton = new Button("Logout");
		logoutButton.setOnAction(e -> logout());
		
		resultArea = new TextArea();
		resultArea.setEditable(false);
		resultArea.setPrefHeight(200);
		resultArea.setStyle("-fx-control-inner-background: #f0f0f0; -fx-font-family: 'Courier New';");

		VBox root = new VBox(10);
		root.setPadding(new Insets(10));
		root.getChildren().addAll(tabPane, resultArea, logoutButton);

		return new Scene(root, 600, 500);
	}

	private void logout() {
		bookingSystem = new FlightBookingSystem();
		resultArea.clear();
		Stage stage = (Stage) resultArea.getScene().getWindow();
		stage.setScene(createLoginScene(stage));
	}

	private GridPane createSearchFlightsPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField originField = new TextField();
		TextField destinationField = new TextField();
		TextField dateField = new TextField();
		Button searchButton = new Button("Search Flights");

		grid.add(new Label("Origin:"), 0, 0);
		grid.add(originField, 1, 0);
		grid.add(new Label("Destination:"), 0, 1);
		grid.add(destinationField, 1, 1);
		grid.add(new Label("Date (YYYY-MM-DD):"), 0, 2);
		grid.add(dateField, 1, 2);
		grid.add(searchButton, 1, 3);

		searchButton.setOnAction(
				e -> searchFlights(originField.getText(), destinationField.getText(), dateField.getText()));

		return grid;
	}

	private GridPane createManageBookingsPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		// Create text fields and buttons
		TextField bookingIdField = new TextField();
		TextField passportField = new TextField();
		Button viewButton = new Button("View Booking");
		Button cancelButton = new Button("Cancel Booking");

		// Add components to grid
		grid.add(new Label("Booking ID:"), 0, 0);
		grid.add(bookingIdField, 1, 0);
		grid.add(new Label("Passport Number:"), 0, 1);
		grid.add(passportField, 1, 1);
		grid.add(viewButton, 1, 2);
		grid.add(cancelButton, 1, 3);

		// View booking event handler
		viewButton.setOnAction(e -> {
			String passportNumber = passportField.getText();
			String bookingIdStr = bookingIdField.getText();

			if (!passportNumber.isEmpty() && !bookingIdStr.isEmpty()) {
				try {
					int bookingId = Integer.parseInt(bookingIdStr);
					viewBookingDetails(passportNumber, bookingId);
				} catch (NumberFormatException ex) {
					resultArea.setText("Please enter a valid booking ID.");
				}
			} else {
				resultArea.setText("Please enter both passport number and booking ID.");
			}
		});

		// Cancel booking event handler
		cancelButton.setOnAction(e -> {
			try {
				int bookingId = Integer.parseInt(bookingIdField.getText());
				cancelBooking(bookingId);
			} catch (NumberFormatException ex) {
				resultArea.setText("Please enter a valid booking ID.");
			}
		});

		return grid;
	}

	private GridPane createModifyBookingPane() {
	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(20, 150, 10, 10));

	    TextField bookingIdField = new TextField();
	    TextField newFlightNumberField = new TextField();
	    TextField passengerNameField = new TextField();
	    TextField passportNumberField = new TextField();
	    TextField contactNumberField = new TextField();
	    Button modifyButton = new Button("Modify Booking");

	    grid.add(new Label("Booking ID:"), 0, 0);
	    grid.add(bookingIdField, 1, 0);
	    grid.add(new Label("New Flight Number:"), 0, 1);
	    grid.add(newFlightNumberField, 1, 1);
	    grid.add(new Label("Passenger Name:"), 0, 2);
	    grid.add(passengerNameField, 1, 2);
	    grid.add(new Label("Passport Number:"), 0, 3);
	    grid.add(passportNumberField, 1, 3);
	    grid.add(new Label("Contact Number:"), 0, 4);
	    grid.add(contactNumberField, 1, 4);
	    grid.add(modifyButton, 1, 5);

	    modifyButton.setOnAction(e -> modifyBooking(
	        bookingIdField.getText(),
	        newFlightNumberField.getText(),
	        passengerNameField.getText(),
	        passportNumberField.getText(),
	        contactNumberField.getText()
	    ));

	    return grid;
	}

	private void modifyBooking(String bookingIdStr, String newFlightNumber, String passengerName, String passportNumber, String contactNumber) {
	    try {
	        int bookingId = Integer.parseInt(bookingIdStr);
	        Passenger updatedPassenger = new Passenger(passengerName, passportNumber, contactNumber);
	        boolean success = bookingSystem.modifyBooking(bookingId, newFlightNumber, updatedPassenger);
	        if (success) {
	            resultArea.setText("Booking " + bookingId + " has been successfully modified.");
	        } else {
	            resultArea.setText("Failed to modify booking. Please check the details and try again.");
	        }
	    } catch (NumberFormatException e) {
	        resultArea.setText("Invalid booking ID. Please enter a valid number.");
	    }
	}
	
	private void viewBookingDetails(String passportNumber, int bookingid) {
		try {
			List<Booking> bookings = bookingSystem.getBookingsByPassportandbookingid(passportNumber, bookingid);
			if (bookings.isEmpty()) {
				resultArea.setText("No bookings found for this passport number.");
				return;
			}

			StringBuilder sb = new StringBuilder();
			sb.append("Bookings found:\n\n");
			for (Booking booking : bookings) {
				sb.append(formatBookingInfo(booking));
				sb.append("\n----------------------------------------\n");
			}
			resultArea.setText(sb.toString());
		} catch (Exception e) {
			resultArea.setText("Error retrieving booking details: " + e.getMessage());
		}
	}

	private void cancelBooking(int bookingId) {
		try {
			boolean cancelled = bookingSystem.cancelBooking(bookingId);
			if (cancelled) {
				resultArea.setText("Booking " + bookingId + " has been cancelled successfully.");
			} else {
				resultArea.setText("Failed to cancel booking. Booking ID not found.");
			}
		} catch (Exception e) {
			resultArea.setText("Error cancelling booking: " + e.getMessage());
		}
	}

	private String formatBookingInfo1(Booking booking) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return String.format(
				"Booking ID: %d\n" + "Flight: %s\n" + "From: %s\n" + "To: %s\n" + "Departure: %s\n" + "Passenger: %s\n"
						+ "Passport: %s\n" + "Contact: %s",
				booking.getBookingId(), booking.getFlight().getFlightNumber(), booking.getFlight().getOrigin(),
				booking.getFlight().getDestination(), booking.getPassenger().getName(),
				booking.getPassenger().getPassportNumber(), booking.getPassenger().getContactNumber());
	}

	private GridPane createBookingPane() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField flightNumberField = new TextField();
		TextField passengerNameField = new TextField();
		TextField passportNumberField = new TextField();
		TextField contactNumberField = new TextField();
		Button bookButton = new Button("Book Flight");

		grid.add(new Label("Flight Number:"), 0, 0);
		grid.add(flightNumberField, 1, 0);
		grid.add(new Label("Passenger Name:"), 0, 1);
		grid.add(passengerNameField, 1, 1);
		grid.add(new Label("Passport Number:"), 0, 2);
		grid.add(passportNumberField, 1, 2);
		grid.add(new Label("Contact Number:"), 0, 3);
		grid.add(contactNumberField, 1, 3);
		grid.add(bookButton, 1, 4);

		bookButton.setOnAction(e -> bookFlight(flightNumberField.getText(), passengerNameField.getText(),
				passportNumberField.getText(), contactNumberField.getText()));

		return grid;
	}

	private void searchFlights(String origin, String destination, String dateStr) {
		try {
			LocalDateTime date = LocalDateTime.parse(dateStr + "T00:00:00");
			List<Flight> flights = bookingSystem.searchFlights(origin, destination, date);

			resultArea.clear();
			if (flights.isEmpty()) {
				resultArea.appendText("No flights found for the given criteria.");
			} else {
				resultArea.appendText("Available Flights:\n\n");
				for (Flight flight : flights) {
					String flightInfo = formatFlightInfo(flight);
					resultArea.appendText(flightInfo);


					resultArea.appendText("\n");
					resultArea.appendText("\n\n");
				}
			}
		} catch (Exception e) {
			resultArea.setText("Error: Invalid date format. Please use YYYY-MM-DD.");
		}
	}

	private void showBookingDialog(Flight flight) {
		Dialog<Passenger> dialog = new Dialog<>();
		dialog.setTitle("Book Flight " + flight.getFlightNumber());
		dialog.setHeaderText("Enter passenger details");

		ButtonType bookButtonType = new ButtonType("Book", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(bookButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField nameField = new TextField();
		TextField passportField = new TextField();
		TextField contactField = new TextField();

		grid.add(new Label("Name:"), 0, 0);
		grid.add(nameField, 1, 0);
		grid.add(new Label("Passport Number:"), 0, 1);
		grid.add(passportField, 1, 1);
		grid.add(new Label("Contact Number:"), 0, 2);
		grid.add(contactField, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == bookButtonType) {
				return new Passenger(nameField.getText(), passportField.getText(), contactField.getText());
			}
			return null;
		});

		Optional<Passenger> result = dialog.showAndWait();

		result.ifPresent(passenger -> {
			bookFlight(flight.getFlightNumber(), passenger.getName(), passenger.getPassportNumber(),
					passenger.getContactNumber());
		});
	}

	private void bookFlight(String flightNumber, String passengerName, String passportNumber, String contactNumber) {
		try {
			Passenger passenger = new Passenger(passengerName, passportNumber, contactNumber);
			Booking booking = bookingSystem.createBooking(flightNumber, passenger);

			if (booking != null) {
				resultArea.setText("Booking successful. Booking ID: " + booking.getBookingId());
			} else {
				resultArea.setText("Booking failed. Please check the flight number and try again.");
			}
		} catch (Exception e) {
			resultArea.setText("Error: " + e.getMessage());
		}
	}

	private void viewBookings(String passportNumber) {
		List<Booking> bookings = bookingSystem.getBookings();

		resultArea.clear();
		if (bookings.isEmpty()) {
			resultArea.appendText("No bookings found for the given passport number.");
		} else {
			resultArea.appendText("Bookings:\n\n");
			for (Booking booking : bookings) {
				resultArea.appendText(formatBookingInfo1(booking));
				resultArea.appendText("\n");
			}
		}
	}

	private void cancelBooking(String bookingIdStr) {
		try {
			int bookingId = Integer.parseInt(bookingIdStr);
			boolean cancelled = bookingSystem.cancelBooking(bookingId);

			if (cancelled) {
				resultArea.setText("Booking " + bookingId + " has been cancelled successfully.");
			} else {
				resultArea.setText("Failed to cancel booking. Booking ID not found.");
			}
		} catch (NumberFormatException e) {
			resultArea.setText("Error: Invalid booking ID. Please enter a valid number.");
		}
	}

	private String formatFlightInfo(Flight flight) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return String.format("Flight %s: %s to %s\n" + "Departure: %s\n" + "Arrival: %s\n" + "Available Seats: %d\n",
				flight.getFlightNumber(), flight.getOrigin(), flight.getDestination(),
				flight.getDepartureTime().format(formatter), flight.getArrivalTime().format(formatter),
				flight.getAvailableSeats());
	}

	private String formatBookingInfo(Booking booking) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return String.format(
				"Booking ID: %d\n" + "Flight: %s\n" + "From: %s\n" + "To: %s\n" + "Departure: %s\n" + "Passenger: %s\n"
						+ "Passport: %s\n" + "Contact: %s",
				booking.getBookingId(), booking.getFlight().getFlightNumber(), booking.getFlight().getOrigin(),
				booking.getFlight().getDestination(), booking.getFlight().getDepartureTime().format(formatter),
				booking.getPassenger().getName(), booking.getPassenger().getPassportNumber(),
				booking.getPassenger().getContactNumber());
	}

	public static void main(String[] args) {
		launch(args);
	}
}