package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.Reply;
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

    @FXML private VBox repliesContainer;
    @FXML private TextArea replyInput;

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

        loadReplies();
    }

    @FXML
    private void handleViewSolution() {
        solutionVisible = !solutionVisible;

        lblSampleSolution.setVisible(solutionVisible);
        lblSampleSolution.setManaged(solutionVisible);

        lblSampleExplanation.setVisible(solutionVisible);
        lblSampleExplanation.setManaged(solutionVisible);
    }

    private void loadReplies() {
        repliesContainer.getChildren().clear();

        if (dailyQuestion == null || dailyQuestion.getReplies() == null) return;

        for (Reply r : dailyQuestion.getReplies()) {
            repliesContainer.getChildren().add(buildReplyNode(r, 0));
        }
    }

    private VBox buildReplyNode(Reply reply, int depth) {
        VBox box = new VBox(3);

        Label author = new Label(
            reply.getAuthor().getUsername() + " • " + reply.getDatePosted()
        );
        author.setStyle("-fx-font-size: 11px; -fx-text-fill: #777;");

        Label content = new Label(reply.getContent());
        content.setWrapText(true);

        box.getChildren().addAll(author, content);

        box.setStyle(
            "-fx-background-color: #f6f6f6;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 6;" +
            "-fx-translate-x: " + (depth * 20) + ";"
        );

        if (reply.getReplies() != null) {
            for (Reply child : reply.getReplies()) {
                box.getChildren().add(buildReplyNode(child, depth + 1));
            }
        }

        return box;
    }

    @FXML
    private void handleAddReply() {
        if (dailyQuestion == null) return;

        String text = replyInput.getText();
        if (text == null || text.isBlank()) return;

        facade.addComment(dailyQuestion, "", text);

        replyInput.clear();
        loadReplies();
    }
}