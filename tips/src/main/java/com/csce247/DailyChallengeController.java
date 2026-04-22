package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.TIPSFacade;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

/**
 * Daily Challenge content controller.
 * Navigation is handled by main.fxml / MainController.
 */
public class DailyChallengeController implements Initializable {

    @FXML private Label challengeDateLabel;
    @FXML private Label challengeTitleLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label courseLabel;
    @FXML private Label languageLabel;
    @FXML private Label statusLabel;
    @FXML private TextArea challengePromptArea;
    @FXML private ListView<String> leaderboardListView;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private Question dailyQuestion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDailyChallenge();
        populateLeaderboard();
        wireInitialViewState();
    }

    private void loadDailyChallenge() {
        User currentUser = facade.getCurrentUser();
        dailyQuestion = facade.getDailyChallenge();

        if (challengeDateLabel != null) {
            challengeDateLabel.setText("Today's Challenge");
        }

        if (dailyQuestion == null) {
            if (challengeTitleLabel != null) challengeTitleLabel.setText("No challenge available");
            if (challengePromptArea != null) {
                challengePromptArea.setText(
                    "No daily challenge is available for the current user.\n\n"
                  + "If this should be available, make sure a student is logged in."
                );
            }
            if (difficultyLabel != null) difficultyLabel.setText("Difficulty: N/A");
            if (courseLabel != null) courseLabel.setText("Course: N/A");
            if (languageLabel != null) languageLabel.setText("Language: N/A");
            if (statusLabel != null) {
                statusLabel.setText(
                    currentUser == null
                            ? "Please log in to see a personalized daily challenge."
                            : "The daily challenge will appear here once a student is logged in."
                );
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

        leaderboardListView.getItems().setAll(
            "No leaderboard entries yet"
        );
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

    public Question getDailyQuestion() {
        return dailyQuestion;
    }
}