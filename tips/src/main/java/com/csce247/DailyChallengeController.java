package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class DailyChallengeController implements Initializable {

    @FXML private Label challengeDateLabel;
    @FXML private Label challengeTitleLabel;
    @FXML private Label difficultyLabel;
    @FXML private Label courseLabel;
    @FXML private Label languageLabel;
    @FXML private TextArea challengePromptArea;

    @FXML private Label lblSampleSolution;
    @FXML private Label lblSampleExplanation;
    @FXML private VBox solutionBox;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private Question dailyQuestion;

    private boolean solutionVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDailyChallenge();
    }

    private void loadDailyChallenge() {
        dailyQuestion = facade.getDailyChallenge();

        challengeDateLabel.setText("Today's Challenge");

        if (dailyQuestion == null) {
            challengeTitleLabel.setText("No challenge available");
            challengePromptArea.setText("No daily challenge available.");
            return;
        }

        challengeTitleLabel.setText(dailyQuestion.getTitle());
        challengePromptArea.setText(dailyQuestion.getPrompt());

        difficultyLabel.setText("Difficulty: " + dailyQuestion.getDifficulty());
        courseLabel.setText("Course: " + dailyQuestion.getCourse());
        languageLabel.setText("Language: " + dailyQuestion.getLanguage());

        if (dailyQuestion.getSampleSolution() != null) {
            lblSampleSolution.setText(dailyQuestion.getSampleSolution());
        }

        if (dailyQuestion.getSampleExplanation() != null) {
            lblSampleExplanation.setText(dailyQuestion.getSampleExplanation());
        }

        lblSampleSolution.setVisible(false);
        lblSampleSolution.setManaged(false);
        lblSampleExplanation.setVisible(false);
        lblSampleExplanation.setManaged(false);
    }

    @FXML
    private void handleViewSolution() {
        solutionVisible = !solutionVisible;

        System.out.println("Toggled solution: " + solutionVisible); // DEBUG

        lblSampleSolution.setVisible(solutionVisible);
        lblSampleSolution.setManaged(solutionVisible);

        lblSampleExplanation.setVisible(solutionVisible);
        lblSampleExplanation.setManaged(solutionVisible);
    }
}