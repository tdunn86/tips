package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Admin;
import com.model.Editor;
import com.model.Student;
import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class DashboardController implements Initializable {

    @FXML private Label greetingLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label streakValueLabel;
    @FXML private Label streakLabel;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardData();
    }

    private void loadDashboardData() {
        User currentUser = facade.getCurrentUser();

        if (currentUser == null) {
            greetingLabel.setText("Hello, Guest! 👋");
            subtitleLabel.setText("Ready to sharpen your coding skills today?");
            streakValueLabel.setText("0 Days");
            streakLabel.setText("Current Streak - Log in to begin!");
            return;
        }

        greetingLabel.setText("Hello, " + currentUser.getUsername() + "! 👋");
        subtitleLabel.setText(buildSubtitle(currentUser));

        int streak = getDisplayedStreak(currentUser);
        streakValueLabel.setText(streak + (streak == 1 ? " Day" : " Days"));
        streakLabel.setText(buildStreakMessage(currentUser, streak));
    }

    private String buildSubtitle(User currentUser) {
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            return "Classification: " + student.getClassification() + " • Ready to sharpen your coding skills today?";
        }
        if (currentUser instanceof Editor) {
            return "Create and manage coding problems for students to practice.";
        }
        if (currentUser instanceof Admin) {
            return "Manage questions, contributors, and platform activity from your dashboard.";
        }
        return "Ready to sharpen your coding skills today?";
    }

    private int getDisplayedStreak(User currentUser) {
        if (currentUser instanceof Student) {
            return ((Student) currentUser).getStreak();
        }
        return 0;
    }

    private String buildStreakMessage(User currentUser, int streak) {
        if (currentUser instanceof Student) {
            return streak > 0 ? "Current Streak - Keep it up!" : "Current Streak - Start today!";
        }
        if (currentUser instanceof Editor) {
            return "Editor account - streak tracking applies to students.";
        }
        if (currentUser instanceof Admin) {
            return "Admin account - streak tracking applies to students.";
        }
        return "Current Streak";
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        navigateWithinShell(event, "question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        navigateWithinShell(event, "dailychallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) {
        navigateWithinShell(event, "contributor.fxml");
    }

    private void navigateWithinShell(ActionEvent event, String fxmlFile) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));

            BorderPane shell = (BorderPane) ((Node) event.getSource()).getScene().getRoot();
            StackPane contentArea = (StackPane) shell.getCenter();

            contentArea.getChildren().setAll(page);
        } catch (Exception e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}