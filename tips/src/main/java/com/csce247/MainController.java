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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

    @FXML private StackPane profileOverlay;
    @FXML private VBox profilePopup;
    @FXML private HBox topNavBar;
    @FXML private Label accountTypeValueLabel;
    @FXML private Label usernameValueLabel;
    @FXML private Label emailValueLabel;
    @FXML private Label userIdValueLabel;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadUsername();
        loadProfileInfo();
        hideProfilePopup();
        updateContributorVisibility(); // ADD THIS

        if (facade.getCurrentUser() == null) {
            showAuthPage("login.fxml");
        } else {
            showPage("dashboard.fxml");
        }
    }

    public void showPage(String fxmlFile) {
        try {
            showTopNav(true);
            loadUsername();
            updateContributorVisibility(); // ADD THIS

            Parent page = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            contentArea.getChildren().setAll(page);
            updateActiveNav(fxmlFile);
            hideProfilePopup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAuthPage(String fxmlFile) {
        try {
            showTopNav(false);  // 🔥 HIDE NAV

            Parent page = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            contentArea.getChildren().setAll(page);

        } catch (IOException e) {
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
            hideProfilePopup();
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
    private void toggleProfilePopup(ActionEvent event) {
        if (profileOverlay.isVisible()) {
            hideProfilePopup();
        } else {
            loadProfileInfo();
            profileOverlay.setVisible(true);
            profileOverlay.setManaged(true);
        }
    }

    @FXML
    private void closeProfilePopup(MouseEvent event) {
        hideProfilePopup();
    }

    @FXML
    private void consumeProfilePopupClick(MouseEvent event) {
        event.consume();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        hideProfilePopup();
        facade.logout();
        showAuthPage("login.fxml");
    }

    private void hideProfilePopup() {
        if (profileOverlay != null) {
            profileOverlay.setVisible(false);
            profileOverlay.setManaged(false);
        }
    }

    private void loadUsername() {
        User currentUser = facade.getCurrentUser();

        if (usernameLabel != null) {
            usernameLabel.setText(currentUser != null ? currentUser.getUsername() : "Guest");
        }

        if (profileButton != null && currentUser != null &&
            currentUser.getUsername() != null && !currentUser.getUsername().isBlank()) {
            profileButton.setText(currentUser.getUsername().substring(0, 1).toUpperCase());
        } else if (profileButton != null) {
            profileButton.setText("G");
        }
    }

    private void loadProfileInfo() {
        if (accountTypeValueLabel == null) return;

        User currentUser = facade.getCurrentUser();

        if (currentUser == null) {
            accountTypeValueLabel.setText("Guest");
            usernameValueLabel.setText("Guest");
            emailValueLabel.setText("No email available");
            userIdValueLabel.setText("-");
            return;
        }

        accountTypeValueLabel.setText(
            currentUser.getAccountType() != null ? currentUser.getAccountType().toString() : "Unknown"
        );
        usernameValueLabel.setText(
            currentUser.getUsername() != null ? currentUser.getUsername() : "Unknown"
        );
        emailValueLabel.setText(
            currentUser.getEmail() != null ? currentUser.getEmail() : "No email available"
        );
        userIdValueLabel.setText(String.valueOf(currentUser.getUserId()));
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
            icon.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
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
            icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #111111;");
        }
        if (text != null) {
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #111111;");
        }
    }

    private void showTopNav(boolean visible) {
        if (topNavBar != null) {
            topNavBar.setVisible(visible);
            topNavBar.setManaged(visible);
        }
    }

    private void updateContributorVisibility() {
        if (contributorNavButton == null) return;

        User currentUser = facade.getCurrentUser();
        boolean canContribute = currentUser != null &&
            (currentUser.getAccountType() == com.model.AccountType.EDITOR ||
            currentUser.getAccountType() == com.model.AccountType.ADMIN);

        contributorNavButton.setVisible(canContribute);
        contributorNavButton.setManaged(canContribute);
    }


}