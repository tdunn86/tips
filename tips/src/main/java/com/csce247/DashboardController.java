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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DashboardController implements Initializable {

    @FXML private Label greetingLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label streakValueLabel;
    @FXML private Label streakLabel;

    @FXML private VBox createQuestionPanel;
    @FXML private Button createQuestionButton;
    @FXML private Button exploreQuestionsButton;
    @FXML private Button startChallengeButton;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    private static final String PRIMARY_BUTTON_STYLE =
        "-fx-min-height: 52; -fx-background-color: #8a0011; -fx-text-fill: white; "
      + "-fx-background-radius: 10; -fx-font-size: 18px; -fx-font-weight: 600;";

    private static final String PRIMARY_BUTTON_HOVER_STYLE =
        "-fx-min-height: 52; -fx-background-color: #a30014; -fx-text-fill: white; "
      + "-fx-background-radius: 10; -fx-font-size: 18px; -fx-font-weight: 600; "
      + "-fx-cursor: hand;";

    private static final String SECONDARY_BUTTON_STYLE =
        "-fx-min-height: 52; -fx-background-color: #f5f5f5; -fx-text-fill: #111111; "
      + "-fx-border-color: rgba(0,0,0,0.15); -fx-border-radius: 10; "
      + "-fx-background-radius: 10; -fx-background-insets: 0; "
      + "-fx-font-size: 18px; -fx-font-weight: 600;";

    private static final String SECONDARY_BUTTON_HOVER_STYLE =
        "-fx-min-height: 52; -fx-background-color: #ececec; -fx-text-fill: #111111; "
      + "-fx-border-color: rgba(0,0,0,0.22); -fx-border-radius: 10; "
      + "-fx-background-radius: 10; -fx-background-insets: 0; "
      + "-fx-font-size: 18px; -fx-font-weight: 600; -fx-cursor: hand;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDashboardData();
        configureCreateQuestionAccess();
        configureButtonHoverEffects();
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

    private void configureCreateQuestionAccess() {
        User currentUser = facade.getCurrentUser();
        boolean canCreate = currentUser instanceof Admin || currentUser instanceof Editor;

        if (createQuestionPanel != null) {
            createQuestionPanel.setVisible(canCreate);
            createQuestionPanel.setManaged(canCreate);
        }

        if (createQuestionButton != null) {
            createQuestionButton.setVisible(canCreate);
            createQuestionButton.setManaged(canCreate);
        }
    }

    private void configureButtonHoverEffects() {
        addHoverEffect(exploreQuestionsButton, PRIMARY_BUTTON_STYLE, PRIMARY_BUTTON_HOVER_STYLE);
        addHoverEffect(startChallengeButton, PRIMARY_BUTTON_STYLE, PRIMARY_BUTTON_HOVER_STYLE);
        addHoverEffect(createQuestionButton, SECONDARY_BUTTON_STYLE, SECONDARY_BUTTON_HOVER_STYLE);
    }

    private void addHoverEffect(Button button, String normalStyle, String hoverStyle) {
        if (button == null) return;

        button.setStyle(normalStyle);

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    @FXML
    private void handleCreateQuestion(ActionEvent event) {
        openAddQuestionPopup();
    }

    private void openAddQuestionPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csce247/addQuestionPopup.fxml"));
            Parent root = loader.load();

            AddQuestionPopupController controller = loader.getController();
            controller.setOnQuestionCreated(this::loadDashboardData);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);

            if (greetingLabel != null && greetingLabel.getScene() != null) {
                popupStage.initOwner(greetingLabel.getScene().getWindow());
            }

            popupStage.setTitle("Create Question");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load addQuestionPopup.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        showPageInShell(event, "question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        showPageInShell(event, "dailyChallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) {
        showPageInShell(event, "contributor.fxml");
    }

    private void showPageInShell(ActionEvent event, String fxmlFile) {
        MainController main = getMainController(event);
        if (main != null) {
            main.showPage(fxmlFile);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csce247/main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            root.setUserData(controller);
            controller.showPage(fxmlFile);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to open " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private MainController getMainController(ActionEvent event) {
        Object controller = ((Node) event.getSource()).getScene().getRoot().getUserData();
        if (controller instanceof MainController) {
            return (MainController) controller;
        }
        return null;
    }
}