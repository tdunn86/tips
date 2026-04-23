package com.csce247;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.TIPSFacade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class QuestionsController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> difficultyFilter;
    @FXML private ComboBox<String> languageFilter;
    @FXML private ComboBox<String> courseFilter;
    @FXML private VBox questionList;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private final List<Question> allQuestions = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadQuestions();

        difficultyFilter.getItems().addAll("All Difficulties", "Easy", "Medium", "Hard");
        difficultyFilter.setValue("All Difficulties");

        languageFilter.getItems().addAll("All Languages", "Java", "C++", "Python", "JavaScript", "HTML", "CSS");
        languageFilter.setValue("All Languages");

        courseFilter.getItems().addAll("All Courses", "CSCE 146", "CSCE 350");
        courseFilter.setValue("All Courses");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        difficultyFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        languageFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        courseFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        applyFilters();
    }

    private void loadQuestions() {
        allQuestions.clear();
        List<Question> questions = facade.getQuestions(null);
        if (questions != null) {
            allQuestions.addAll(questions);
        }
    }

    private void applyFilters() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        String diff = difficultyFilter.getValue();
        String lang = languageFilter.getValue();
        String course = courseFilter.getValue();

        questionList.getChildren().clear();

        boolean anyVisible = false;

        for (Question q : allQuestions) {
            String title = safe(q.getTitle());
            String qDiff = displayDifficulty(q.getDifficulty());
            String qLang = displayLanguage(q.getLanguage());
            String qCourse = displayCourse(q.getCourse());

            boolean matchSearch = title.toLowerCase().contains(search);
            boolean matchDiff = diff == null || diff.equals("All Difficulties") || qDiff.equalsIgnoreCase(diff);
            boolean matchLang = lang == null || lang.equals("All Languages") || qLang.equalsIgnoreCase(lang);
            boolean matchCourse = course == null || course.equals("All Courses") || qCourse.equalsIgnoreCase(course);

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
        Label badge = new Label(displayDifficulty(q.getDifficulty()));
        String badgeStyle;
        switch (displayDifficulty(q.getDifficulty())) {
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

        Label titleLabel = new Label(safe(q.getTitle()));
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        HBox titleRow = new HBox(10, titleLabel, badge);
        titleRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label langLabel = new Label("Language: " + displayLanguage(q.getLanguage()));
        Label dot = new Label("•");
        Label courseLabel = new Label("Course: " + displayCourse(q.getCourse()));

        langLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");
        dot.setStyle("-fx-font-size: 12px; -fx-text-fill: #bbbbbb;");
        courseLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        HBox metaRow = new HBox(8, langLabel, dot, courseLabel);
        metaRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        VBox info = new VBox(6, titleRow, metaRow);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button solveBtn = new Button("Solve");
        solveBtn.setStyle("-fx-background-color: #8b1a1a; -fx-text-fill: white;" +
                "-fx-font-size: 13px; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-cursor: hand;" +
                "-fx-padding: 9 22 9 22;");
        solveBtn.setOnAction(e -> handleSolve(e, q));

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

    private void handleSolve(ActionEvent event, Question question) {
        MainController main = getMainController(event);
        if (main != null) {
            main.showQuestionDetail(question);
        } else {
            System.err.println("MainController not found.");
        }
    }

    private MainController getMainController(ActionEvent event) {
        Parent root = ((Node) event.getSource()).getScene().getRoot();
        Object controller = root.getUserData();
        if (controller instanceof MainController) {
            return (MainController) controller;
        }
        return null;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String displayDifficulty(Object difficulty) {
        if (difficulty == null) return "N/A";
        String raw = difficulty.toString().trim();
        if (raw.isEmpty()) return "N/A";
        return capitalize(raw);
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