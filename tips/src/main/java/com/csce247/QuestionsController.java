package com.csce247;

import com.model.Question;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class QuestionsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> difficultyFilter;
    @FXML private ComboBox<String> languageFilter;
    @FXML private ComboBox<String> courseFilter;
    @FXML private VBox questionList;

    @FXML private Button btnHome;
    @FXML private Button btnQuestions;
    @FXML private Button btnDailyChallenge;
    @FXML private Button btnContributor;

    private List<Question> allQuestions = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ✅ Load real questions from the facade
        allQuestions = TIPSFacade.getInstance().getQuestions(null);

        // Populate filter options dynamically
        difficultyFilter.getItems().add("All Difficulties");
        for (com.model.Difficulty d : com.model.Difficulty.values())
            difficultyFilter.getItems().add(capitalize(d.toString()));
        difficultyFilter.setValue("All Difficulties");

        languageFilter.getItems().add("All Languages");
        for (com.model.Language l : com.model.Language.values())
            languageFilter.getItems().add(l.toString());
        languageFilter.setValue("All Languages");

        courseFilter.getItems().add("All Courses");
        for (com.model.Course c : com.model.Course.values())
            courseFilter.getItems().add(c.toString().replace("_", " "));
        courseFilter.setValue("All Courses");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        difficultyFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        languageFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        courseFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        applyFilters();
    }

    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String diff   = difficultyFilter.getValue();
        String lang   = languageFilter.getValue();
        String course = courseFilter.getValue();

        questionList.getChildren().clear();

        boolean anyVisible = false;

        for (Question q : allQuestions) {
            String qDiff   = q.getDifficulty() != null ? capitalize(q.getDifficulty().toString()) : "";
            String qLang   = q.getLanguage()   != null ? q.getLanguage().toString() : "";
            String qCourse = q.getCourse()     != null ? q.getCourse().toString().replace("_", " ") : "";
            String qTitle  = q.getTitle()      != null ? q.getTitle().toLowerCase() : "";

            boolean matchSearch = qTitle.contains(search);
            boolean matchDiff   = diff   == null || diff.equals("All Difficulties") || qDiff.equals(diff);
            boolean matchLang   = lang   == null || lang.equals("All Languages")    || qLang.equals(lang);
            boolean matchCourse = course == null || course.equals("All Courses")    || qCourse.equals(course);

            if (matchSearch && matchDiff && matchLang && matchCourse) {
                questionList.getChildren().add(buildCard(q));
                anyVisible = true;
            }
        }

        if (!anyVisible) {
            questionList.getChildren().add(buildEmptyState());
        }
    }

    private HBox buildCard(Question q) {
        String diffStr = q.getDifficulty() != null ? capitalize(q.getDifficulty().toString()) : "";

        Label badge = new Label(diffStr);
        String badgeStyle;
        switch (diffStr) {
            case "Easy":
                badgeStyle = "-fx-background-color: #d4edda; -fx-text-fill: #276239;";
                break;
            case "Medium":
                badgeStyle = "-fx-background-color: #fff3cd; -fx-text-fill: #856404;";
                break;
            case "Hard":
                badgeStyle = "-fx-background-color: #fde8e8; -fx-text-fill: #b91c1c;";
                break;
            default:
                badgeStyle = "";
        }
        badge.setStyle(badgeStyle +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-background-radius: 999; -fx-padding: 2 9 2 9;");

        Label titleLabel = new Label(q.getTitle() != null ? q.getTitle() : "Untitled");
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        HBox titleRow = new HBox(10, titleLabel, badge);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        String langStr   = q.getLanguage() != null ? q.getLanguage().toString() : "";
        String courseStr = q.getCourse()   != null ? q.getCourse().toString().replace("_", " ") : "";

        Label langLabel   = new Label("Language: " + langStr);
        Label dot         = new Label("•");
        Label courseLabel = new Label("Course: " + courseStr);
        langLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: #bbbbbb;");
        courseLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        HBox metaRow = new HBox(8, langLabel, dot, courseLabel);
        metaRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(6, titleRow, metaRow);
        HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

        Button solveBtn = new Button("Solve");
        solveBtn.setStyle("-fx-background-color: #8b1a1a; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-padding: 9 22 9 22;");
        solveBtn.setOnAction(e -> handleSolve(q)); // ✅ pass the real Question object

        HBox card = new HBox(info, solveBtn);
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;" +
                "-fx-border-width: 1; -fx-background-radius: 10;" +
                "-fx-border-radius: 10; -fx-padding: 20 24 20 24;");

        return card;
    }

    private Label buildEmptyState() {
        Label empty = new Label("No questions match your filters.");
        empty.setStyle("-fx-text-fill: #888888; -fx-font-size: 14px; -fx-padding: 40 0 40 0;");
        return empty;
    }

    private void handleSolve(Question q) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/csce247/questionDetail.fxml"));
            Parent root = loader.load();
            QuestionDetailController controller = loader.getController();
            controller.setQuestion(q); // ✅ pass real com.model.Question directly
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Navigation failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleHome() {
        navigateTo("home.fxml");
    }

    @FXML
    private void handleDailyChallenge() {
        navigateTo("dailyChallenge.fxml");
    }

    @FXML
    private void handleContributor() {
        navigateTo("contributor.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            Stage stage = (Stage) searchField.getScene().getWindow();
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