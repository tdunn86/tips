package com.csce247;

import com.model.Question;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class QuestionDetailController implements Initializable {

    // ------------------------------------------------------------------ //
    //  FXML-injected nodes                                                //
    // ------------------------------------------------------------------ //

    @FXML private Label lblTitle;
    @FXML private Label lblDifficulty;
    @FXML private Label lblLanguage;
    @FXML private Label lblCourse;
    @FXML private Label lblPrompt;
    @FXML private Label lblHint;
    @FXML private Label lblSampleSolution;
    @FXML private Label lblSampleExplanation;
    @FXML private VBox  hintBox;
    @FXML private VBox  solutionBox;

    // ------------------------------------------------------------------ //
    //  Internal state                                                     //
    // ------------------------------------------------------------------ //

    private Question currentQuestion;
    private boolean hintVisible     = false;
    private boolean solutionVisible = false;

    // ------------------------------------------------------------------ //
    //  Initializable                                                      //
    // ------------------------------------------------------------------ //

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Content is populated via setQuestion(), not here
    }

    // ------------------------------------------------------------------ //
    //  Public API — called by QuestionsController before showing scene   //
    // ------------------------------------------------------------------ //

    public void setQuestion(Question q) {
        this.currentQuestion = q;

        lblTitle.setText(q.getTitle() != null ? q.getTitle() : "Untitled");

        // Difficulty badge text + colour
        if (q.getDifficulty() != null) {
            String diff = capitalize(q.getDifficulty().toString());
            lblDifficulty.setText(diff);
            switch (diff) {
                case "Easy":
                    lblDifficulty.setStyle(lblDifficulty.getStyle()
                        + "-fx-background-color: #d4edda; -fx-text-fill: #276239;");
                    break;
                case "Medium":
                    lblDifficulty.setStyle(lblDifficulty.getStyle()
                        + "-fx-background-color: #fff3cd; -fx-text-fill: #856404;");
                    break;
                case "Hard":
                    lblDifficulty.setStyle(lblDifficulty.getStyle()
                        + "-fx-background-color: #fde8e8; -fx-text-fill: #b91c1c;");
                    break;
                default:
                    break;
            }
        }

        lblLanguage.setText(q.getLanguage() != null ? q.getLanguage().toString() : "");
        lblCourse.setText(q.getCourse()   != null ? q.getCourse().toString().replace("_", " ") : "");
        lblPrompt.setText(q.getPrompt()   != null ? q.getPrompt() : "No description provided.");

        // Hint — hide section if none
        if (q.getHint() != null && !q.getHint().isEmpty()) {
            lblHint.setText(q.getHint());
        } else {
            hintBox.setVisible(false);
            hintBox.setManaged(false);
        }

        // Sample solution — hide section if none
        if (q.getSampleSolution() != null && !q.getSampleSolution().isEmpty()) {
            lblSampleSolution.setText(q.getSampleSolution());
            lblSampleExplanation.setText(
                q.getSampleExplanation() != null ? q.getSampleExplanation() : "");
        } else {
            solutionBox.setVisible(false);
            solutionBox.setManaged(false);
        }
    }

    // ------------------------------------------------------------------ //
    //  Toggle handlers                                                    //
    // ------------------------------------------------------------------ //

    @FXML
    private void handleToggleHint() {
        hintVisible = !hintVisible;
        lblHint.setVisible(hintVisible);
        lblHint.setManaged(hintVisible);
    }

    @FXML
    private void handleViewSolution() {
        solutionVisible = !solutionVisible;
        lblSampleSolution.setVisible(solutionVisible);
        lblSampleSolution.setManaged(solutionVisible);
        lblSampleExplanation.setVisible(solutionVisible);
        lblSampleExplanation.setManaged(solutionVisible);
    }

    // ------------------------------------------------------------------ //
    //  Navigation                                                         //
    // ------------------------------------------------------------------ //

    @FXML
    private void handleBackToQuestions() {
        navigateTo("question.fxml");
    }

    @FXML
    private void handleNavHome() {
        navigateTo("home.fxml");
    }

    @FXML
    private void handleNavQuestions() {
        navigateTo("question.fxml");
    }

    @FXML
    private void handleNavDailyChallenge() {
        navigateTo("dailyChallenge.fxml");
    }

    @FXML
    private void handleNavContributor() {
        navigateTo("contributor.fxml");
    }

    @FXML
    private void handleNavLogout() {
        TIPSFacade.getInstance().logout();
        navigateTo("login.fxml");
    }

    // ------------------------------------------------------------------ //
    //  Utility                                                            //
    // ------------------------------------------------------------------ //

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            Stage stage = (Stage) lblTitle.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Navigation failed: " + fxmlFile + " — " + e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
}