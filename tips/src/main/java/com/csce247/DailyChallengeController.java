package com.csce247;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.model.Question;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller for the Daily Challenge page.
 * Pulls the logged-in user and today's question from the facade,
 * then populates the view without hardcoded user or question data.
 */
public class DailyChallengeController implements Initializable {

    @FXML private Label usernameLabel;
    @FXML private Label challengeDateLabel;
    @FXML private Label challengeTitleLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label courseLabel;
    @FXML private Label languageLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea challengePromptArea;
    @FXML private ListView<String> leaderboardListView;

    @FXML private Button btnHome;
    @FXML private Button btnQuestions;
    @FXML private Button btnDailyChallenge;
    @FXML private Button btnContributor;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private Question dailyQuestion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUsername();
        loadDailyChallenge();
        populateLeaderboard();
        wireInitialViewState();
    }

    private void loadUsername() {
        User currentUser = facade.getCurrentUser();
        if (usernameLabel == null) return;

        if (currentUser != null) {
            usernameLabel.setText(currentUser.getUsername());
        } else {
            usernameLabel.setText("Guest");
        }
    }

    private void loadDailyChallenge() {
        dailyQuestion = facade.getDailyChallenge();

        if (challengeDateLabel != null) {
            challengeDateLabel.setText("Today's Challenge");
        }

        if (dailyQuestion == null) {
            if (challengeTitleLabel != null) challengeTitleLabel.setText("No challenge available");
            if (challengePromptArea != null) {
                challengePromptArea.setText(
                        "No daily challenge is available for the current user.\n\n"
                      + "If this should be available, make sure a student is logged in.");
            }
            if (difficultyLabel != null) difficultyLabel.setText("Difficulty: N/A");
            if (courseLabel != null) courseLabel.setText("Course: N/A");
            if (languageLabel != null) languageLabel.setText("Language: N/A");
            if (statusLabel != null) {
                statusLabel.setText("The daily challenge will appear here once a student is logged in.");
            }
            return;
        }

        if (challengeTitleLabel != null) challengeTitleLabel.setText(nullToNA(dailyQuestion.getTitle()));
        if (challengePromptArea != null) challengePromptArea.setText(nullToNA(dailyQuestion.getPrompt()));
        if (difficultyLabel != null) difficultyLabel.setText("Difficulty: " + nullToNA(String.valueOf(dailyQuestion.getDifficulty())));
        if (courseLabel != null) courseLabel.setText("Course: " + nullToNA(String.valueOf(dailyQuestion.getCourse())));
        if (languageLabel != null) languageLabel.setText("Language: " + nullToNA(String.valueOf(dailyQuestion.getLanguage())));
        if (statusLabel != null) statusLabel.setText("Your daily challenge is ready.");
    }

    private void populateLeaderboard() {
        if (leaderboardListView == null) return;

        leaderboardListView.getItems().setAll(List.of(
            "No leaderboard entries yet"
        ));
    }

    private void wireInitialViewState() {
        if (challengePromptArea != null) {
            challengePromptArea.setEditable(false);
            challengePromptArea.setWrapText(true);
        }
    }

    private String nullToNA(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        navigate(event, "dashboard.fxml");
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        navigate(event, "question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        navigate(event, "dailychallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) {
        navigate(event, "contributor.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        facade.logout();
        navigate(event, "login.fxml");
    }

    private void navigate(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Navigation failed to " + fxmlFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Placeholder for a future solution page.
     */
    @FXML
    private void handleRevealSolution() {
        // TODO: navigate to a future solution page.
    }

    public Question getDailyQuestion() {
        return dailyQuestion;
    }
}