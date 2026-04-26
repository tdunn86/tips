package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class QuestionDetailController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblDifficulty;
    @FXML private Label lblLanguage;
    @FXML private Label lblCourse;
    @FXML private Label lblPrompt;

    @FXML private VBox hintBox;
    @FXML private Label lblHint;
    @FXML private Button btnToggleHint;

    @FXML private VBox solutionBox;
    @FXML private Label lblSampleSolution;
    @FXML private Label lblSampleExplanation;
    @FXML private Button btnViewSolution;

    private Question currentQuestion;
    private boolean hintVisible = false;
    private boolean solutionVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (hintBox != null) {
            hintBox.setVisible(false);
            hintBox.setManaged(false);
        }
        if (solutionBox != null) {
            solutionBox.setVisible(false);
            solutionBox.setManaged(false);
        }
    }

    public void setQuestion(Question q) {
        this.currentQuestion = q;

        if (q == null) {
            lblTitle.setText("Untitled");
            lblDifficulty.setText("N/A");
            lblLanguage.setText("N/A");
            lblCourse.setText("N/A");
            lblPrompt.setText("No description available.");
            return;
        }

        lblTitle.setText(safe(q.getTitle()));

        String diff = displayDifficulty(q.getDifficulty());
        lblDifficulty.setText(diff);
        applyDifficultyStyle(diff);

        lblLanguage.setText(displayLanguage(q.getLanguage()));
        lblCourse.setText(displayCourse(q.getCourse()));
        lblPrompt.setText(safe(q.getPrompt()).isBlank() ? "No description provided." : safe(q.getPrompt()));

        hintVisible = false;
        solutionVisible = false;

        String hint = safe(q.getHint());
        if (!hint.isBlank()) {
            hintBox.setVisible(true);
            hintBox.setManaged(true);
            lblHint.setText(hint);
            lblHint.setVisible(false);
            lblHint.setManaged(false);
            btnToggleHint.setText("Show Hint");
            btnToggleHint.setDisable(false);
        } else {
            hintBox.setVisible(false);
            hintBox.setManaged(false);
        }

        String sampleSolution = safe(q.getSampleSolution());
        if (!sampleSolution.isBlank()) {
            solutionBox.setVisible(true);
            solutionBox.setManaged(true);
            lblSampleSolution.setText(sampleSolution);
            lblSampleExplanation.setText(safe(q.getSampleExplanation()));
            lblSampleSolution.setVisible(false);
            lblSampleSolution.setManaged(false);
            lblSampleExplanation.setVisible(false);
            lblSampleExplanation.setManaged(false);
            btnViewSolution.setText("View Solution");
            btnViewSolution.setDisable(false);
        } else {
            solutionBox.setVisible(false);
            solutionBox.setManaged(false);
        }
    }

    @FXML
    private void handleBackToQuestions(ActionEvent event) {
        MainController main = getMainController();
        if (main != null) {
            main.showPage("question.fxml");
        }
    }

    @FXML
    private void handleToggleHint(ActionEvent event) {
        if (hintBox == null || !hintBox.isVisible()) {
            return;
        }

        hintVisible = !hintVisible;
        lblHint.setVisible(hintVisible);
        lblHint.setManaged(hintVisible);
        btnToggleHint.setText(hintVisible ? "Hide Hint" : "Show Hint");
    }

    @FXML
    private void handleViewSolution(ActionEvent event) {
        if (solutionBox == null || !solutionBox.isVisible()) {
            return;
        }

        solutionVisible = !solutionVisible;
        lblSampleSolution.setVisible(solutionVisible);
        lblSampleSolution.setManaged(solutionVisible);
        lblSampleExplanation.setVisible(solutionVisible);
        lblSampleExplanation.setManaged(solutionVisible);
        btnViewSolution.setText(solutionVisible ? "Hide Solution" : "View Solution");
    }

    private MainController getMainController() {
        if (lblTitle == null || lblTitle.getScene() == null) {
            return null;
        }
        Parent root = lblTitle.getScene().getRoot();
        Object controller = root.getUserData();
        if (controller instanceof MainController) {
            return (MainController) controller;
        }
        return null;
    }

    private void applyDifficultyStyle(String diff) {
        String base = "-fx-font-size: 11px; -fx-font-weight: bold; -fx-background-radius: 999; -fx-padding: 3 10 3 10;";
        switch (diff) {
            case "Easy":
                lblDifficulty.setStyle(base + "-fx-background-color: #d4edda; -fx-text-fill: #276239;");
                break;
            case "Medium":
                lblDifficulty.setStyle(base + "-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                break;
            case "Hard":
                lblDifficulty.setStyle(base + "-fx-background-color: #fde8e8; -fx-text-fill: #b91c1c;");
                break;
            default:
                lblDifficulty.setStyle(base);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String displayDifficulty(Object difficulty) {
        if (difficulty == null) return "N/A";
        String raw = difficulty.toString().trim();
        return raw.isEmpty() ? "N/A" : capitalize(raw);
    }

    private String displayLanguage(Object language) {
        if (language == null) return "N/A";
        String raw = language.toString().trim();
        if (raw.isEmpty()) return "N/A";

        if (raw.equalsIgnoreCase("CPP")) return "C++";
        if (raw.equalsIgnoreCase("JAVA")) return "Java";
        if (raw.equalsIgnoreCase("PYTHON")) return "Python";
        if (raw.equalsIgnoreCase("JAVASCRIPT")) return "JavaScript";
        if (raw.equalsIgnoreCase("HTML")) return "HTML";
        if (raw.equalsIgnoreCase("CSS")) return "CSS";

        return capitalize(raw);
    }

    private String displayCourse(Object course) {
        if (course == null) return "N/A";
        String raw = course.toString().trim();
        if (raw.isEmpty()) return "N/A";
        raw = raw.replace('_', ' ');
        raw = raw.replaceAll("(?i)([A-Z]+)(\\d+)", "$1 $2");
        return raw;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}