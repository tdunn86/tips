package com.csce247;

import com.model.Question;
import com.model.Student;
import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label greetingLabel;
    @FXML private Label userNameLabel;
    @FXML private Label streakLabel;
    @FXML private Label solvedCountLabel;

    private TIPSFacade facade;

    @FXML
    public void initialize() {
        facade = TIPSFacade.getInstance();
        User user = facade.getCurrentUser();

        if (user != null) {
            userNameLabel.setText(user.getUsername());
            greetingLabel.setText("Hello, " + user.getUsername() + "! 👋");
            
            // Check if user is a student to show streak/progress
            if (user instanceof Student) {
                Student student = (Student) user;
                streakLabel.setText(student.getStreak() + " Days");
                // solvedCountLabel.setText(String.valueOf(student.getSolvedQuestions().size()));
            }
        }
    }

    @FXML
    private void handleViewQuestions(ActionEvent event) {
        System.out.println("Navigating to Question List...");
        // In a real app, use: App.setRoot("questions");
        facade.getQuestions(null); 
    }

    @FXML
    private void handleDailyChallenge(ActionEvent event) {
        Question daily = facade.getDailyChallenge();
        if (daily != null) {
            System.out.println("Starting: " + daily.getTitle());
        }
    }

    @FXML
    private void handleAddQuestion(ActionEvent event) {
        System.out.println("Opening Add Question Dialog...");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        facade.logout();
        try {
            App.setRoot("login"); // Assuming your main class is App
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}