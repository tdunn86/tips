package com.csce247;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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

    private static class Question {
        String title, difficulty, language, course;

        Question(String title, String difficulty, String language, String course) {
            this.title = title;
            this.difficulty = difficulty;
            this.language = language;
            this.course = course;
        }
    }

    private final List<Question> allQuestions = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allQuestions.add(new Question("Two Sum",                        "Easy",   "Java", "CSCE 146"));
        allQuestions.add(new Question("Valid Parentheses",              "Easy",   "Java", "CSCE 146"));
        allQuestions.add(new Question("Merge Two Sorted Lists",         "Easy",   "Java", "CSCE 146"));
        allQuestions.add(new Question("Binary Search Tree Validation",  "Medium", "C++",  "CSCE 350"));
        allQuestions.add(new Question("Longest Palindromic Substring",  "Medium", "C++",  "CSCE 350"));
        allQuestions.add(new Question("Graph Cycle Detection",          "Hard",   "C++",  "CSCE 350"));

        difficultyFilter.getItems().addAll("All Difficulties", "Easy", "Medium", "Hard");
        difficultyFilter.setValue("All Difficulties");

        languageFilter.getItems().addAll("All Languages", "Java", "C++");
        languageFilter.setValue("All Languages");

        courseFilter.getItems().addAll("All Courses", "CSCE 146", "CSCE 350");
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
            boolean matchSearch = q.title.toLowerCase().contains(search);
            boolean matchDiff   = diff   == null || diff.equals("All Difficulties") || q.difficulty.equals(diff);
            boolean matchLang   = lang   == null || lang.equals("All Languages")    || q.language.equals(lang);
            boolean matchCourse = course == null || course.equals("All Courses")    || q.course.equals(course);

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
    Label badge = new Label(q.difficulty);
    String badgeStyle;
    switch (q.difficulty) {
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

        Label titleLabel = new Label(q.title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        HBox titleRow = new HBox(10, titleLabel, badge);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label langLabel   = new Label("Language: " + q.language);
        Label dot         = new Label("•");
        Label courseLabel = new Label("Course: " + q.course);
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
        solveBtn.setOnAction(e -> handleSolve(q.title));

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

    private void handleSolve(String questionTitle) {
        // TODO: navigate to solve view
        System.out.println("Solving: " + questionTitle);
    }

    @FXML
    private void handleHome() {
        System.out.println("Navigate to Home");
    }

    @FXML
    private void handleDailyChallenge() {
        System.out.println("Navigate to Daily Challenge");
    }

    @FXML
    private void handleContributor() {
        navigateTo("contributor.fxml");
        System.out.println("Navigate to Contributor");
    }
    private void navigateTo(String fxmlFile) {
    try {
        javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
        javafx.stage.Stage stage = (javafx.stage.Stage) searchField.getScene().getWindow();
        stage.setScene(new javafx.scene.Scene(root));
    } catch (Exception e) {
        System.err.println("Navigation failed: " + fxmlFile + " — " + e.getMessage());
    }
}
}