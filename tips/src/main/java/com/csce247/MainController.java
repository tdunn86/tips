package com.csce247;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;
    @FXML private Label usernameLabel;

    @FXML private Button homeNavButton;
    @FXML private Button questionsNavButton;
    @FXML private Button dailyChallengeNavButton;
    @FXML private Button contributorNavButton;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUsername();
        goDashboard(null);
    }

    private void loadUsername() {
        User currentUser = facade.getCurrentUser();
        if (usernameLabel != null) {
            usernameLabel.setText(currentUser != null ? currentUser.getUsername() : "Guest");
        }
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        loadPage("dashboard.fxml");
        setActive(homeNavButton);
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        loadPage("question.fxml");
        setActive(questionsNavButton);
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        loadPage("dailychallenge.fxml");
        setActive(dailyChallengeNavButton);
    }

    @FXML
    private void goContributor(ActionEvent event) {
        loadPage("contributor.fxml");
        setActive(contributorNavButton);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        facade.logout();
        loadPage("login.fxml");
        clearActive();
    }

    private void loadPage(String fxmlFile) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearActive() {
        setStyle(homeNavButton, false);
        setStyle(questionsNavButton, false);
        setStyle(dailyChallengeNavButton, false);
        setStyle(contributorNavButton, false);
    }

    private void setActive(Button active) {
        clearActive();
        setStyle(active, true);
    }

    private void setStyle(Button button, boolean active) {
        if (button == null) return;

        if (active) {
            button.setStyle("-fx-background-color: #8a0011; -fx-background-radius: 14; -fx-padding: 12 18 12 18; -fx-min-height: 46; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-background-radius: 14; -fx-padding: 12 18 12 18; -fx-min-height: 46; -fx-alignment: CENTER_LEFT; -fx-cursor: hand;");
        }
    }
}