package com.csce247;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainController implements Initializable {

    @FXML private StackPane contentArea;

    @FXML private Button logoButton;
    @FXML private Button homeNavButton;
    @FXML private Button questionsNavButton;
    @FXML private Button dailyChallengeNavButton;
    @FXML private Button contributorNavButton;
    @FXML private Button profileButton;

    @FXML private Label usernameLabel;

    @FXML private Label homeIconLabel;
    @FXML private Label homeTextLabel;
    @FXML private Label questionsIconLabel;
    @FXML private Label questionsTextLabel;
    @FXML private Label dailyChallengeIconLabel;
    @FXML private Label dailyChallengeTextLabel;
    @FXML private Label contributorIconLabel;
    @FXML private Label contributorTextLabel;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUsername();
        showPage("dashboard.fxml");
    }

    public void showPage(String fxmlFile) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            contentArea.getChildren().setAll(page);
            updateActiveNav(fxmlFile);
        } catch (IOException e) {
            System.err.println("Failed to load " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showQuestionDetail(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csce247/questiondetail.fxml"));
            Parent page = loader.load();

            QuestionDetailController controller = loader.getController();
            controller.setQuestion(question);

            contentArea.getChildren().setAll(page);
            updateActiveNav("question.fxml");
        } catch (IOException e) {
            System.err.println("Failed to load questiondetail.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        showPage("dashboard.fxml");
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        showPage("question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        showPage("dailyChallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) {
        showPage("contributor.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        facade.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load login.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsername() {
        User currentUser = facade.getCurrentUser();
        if (usernameLabel != null) {
            usernameLabel.setText(currentUser != null ? currentUser.getUsername() : "Guest");
        }
    }

    private void updateActiveNav(String fxmlFile) {
        clearActiveStyles();

        if ("dashboard.fxml".equals(fxmlFile)) {
            setActive(homeNavButton, homeIconLabel, homeTextLabel);
        } else if ("question.fxml".equals(fxmlFile)) {
            setActive(questionsNavButton, questionsIconLabel, questionsTextLabel);
        } else if ("dailyChallenge.fxml".equals(fxmlFile)) {
            setActive(dailyChallengeNavButton, dailyChallengeIconLabel, dailyChallengeTextLabel);
        } else if ("contributor.fxml".equals(fxmlFile)) {
            setActive(contributorNavButton, contributorIconLabel, contributorTextLabel);
        }
    }

    private void clearActiveStyles() {
        setInactive(homeNavButton, homeIconLabel, homeTextLabel);
        setInactive(questionsNavButton, questionsIconLabel, questionsTextLabel);
        setInactive(dailyChallengeNavButton, dailyChallengeIconLabel, dailyChallengeTextLabel);
        setInactive(contributorNavButton, contributorIconLabel, contributorTextLabel);
    }

    private void setActive(Button button, Label icon, Label text) {
        if (button != null) {
            button.setStyle(
                "-fx-background-color: #8a0011;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 12 18 12 18;" +
                "-fx-min-height: 46;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-cursor: hand;"
            );
        }
        if (icon != null) {
            icon.setStyle("-fx-font-size: 18px; -fx-min-width: 20; -fx-min-height: 20; -fx-text-fill: white;");
        }
        if (text != null) {
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: white;");
        }
    }

    private void setInactive(Button button, Label icon, Label text) {
        if (button != null) {
            button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 14;" +
                "-fx-padding: 12 18 12 18;" +
                "-fx-min-height: 46;" +
                "-fx-alignment: CENTER_LEFT;" +
                "-fx-cursor: hand;"
            );
        }
        if (icon != null) {
            icon.setStyle("-fx-font-size: 18px; -fx-min-width: 20; -fx-min-height: 20; -fx-text-fill: #111111;");
        }
        if (text != null) {
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #111111;");
        }
    }
}