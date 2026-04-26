package com.csce247;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.model.Course;
import com.model.Difficulty;
import com.model.Language;
import com.model.Question;
import com.model.Solution;
import com.model.TIPSFacade;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Contributor page content controller.
 * Navigation is handled by main.fxml / MainController.
 */
public class ContributorController implements Initializable {

    @FXML private Label lblProblemsSubmitted;
    @FXML private Label lblSolutionsWritten;
    @FXML private Label lblApprovalRate;

    @FXML private StackPane tabApproved;
    @FXML private StackPane tabPending;
    @FXML private StackPane tabRejected;

    @FXML private Label lblTabApproved;
    @FXML private Label lblTabPending;
    @FXML private Label lblTabRejected;

    @FXML private VBox questionListVBox;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private User currentUser;

    private String activeTab = "approved";

    private final List<Question> approvedQuestions = new ArrayList<>();
    private final List<Question> pendingQuestions = new ArrayList<>();
    private final List<Question> rejectedQuestions = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentUser = facade.getCurrentUser();

        loadQuestions();
        updateStatCards();
        renderTab(activeTab);
    }

    private void loadQuestions() {
        approvedQuestions.clear();
        pendingQuestions.clear();
        rejectedQuestions.clear();

        if (currentUser == null) {
            return;
        }

        List<Question> all = facade.getQuestions("");
        for (Question q : all) {
            if (q.getAuthor() == null) {
                continue;
            }
            if (q.getAuthor().getUserId() != currentUser.getUserId()) {
                continue;
            }

            if (q.isSolutionRevealed()) {
                approvedQuestions.add(q);
            } else if (q.getSolutions() == null || q.getSolutions().isEmpty()) {
                pendingQuestions.add(q);
            } else {
                rejectedQuestions.add(q);
            }
        }
    }

    private void updateStatCards() {
        int totalSubmitted = approvedQuestions.size() + pendingQuestions.size() + rejectedQuestions.size();
        int solutionsWritten = countSolutionsWritten();

        int approvalRate = totalSubmitted == 0
                ? 0
                : (int) Math.round((approvedQuestions.size() * 100.0) / totalSubmitted);

        lblProblemsSubmitted.setText(String.valueOf(totalSubmitted));
        lblSolutionsWritten.setText(String.valueOf(solutionsWritten));
        lblApprovalRate.setText(approvalRate + "%");
    }

    private int countSolutionsWritten() {
        if (currentUser == null) {
            return 0;
        }

        List<Question> all = facade.getQuestions("");
        int count = 0;

        for (Question q : all) {
            if (q.getSolutions() == null) {
                continue;
            }

            for (Solution s : q.getSolutions()) {
                if (s.getAuthor() != null && s.getAuthor().getUserId() == currentUser.getUserId()) {
                    count++;
                }
            }
        }
        return count;
    }

    @FXML
    private void onTabApproved() {
        activeTab = "approved";
        renderTab(activeTab);
    }

    @FXML
    private void onTabPending() {
        activeTab = "pending";
        renderTab(activeTab);
    }

    @FXML
    private void onTabRejected() {
        activeTab = "rejected";
        renderTab(activeTab);
    }

    private void renderTab(String tab) {
        lblTabApproved.setText("◔  Approved (" + approvedQuestions.size() + ")");
        lblTabPending.setText("◔  Pending Review (" + pendingQuestions.size() + ")");
        lblTabRejected.setText("⊗  Rejected (" + rejectedQuestions.size() + ")");

        String activeStyle = "-fx-background-color: white; -fx-background-radius: 12; "
                + "-fx-border-color: #d0d0d0; -fx-border-radius: 12; -fx-border-width: 1;";
        String inactiveStyle = "-fx-background-color: transparent;";

        tabApproved.setStyle(tab.equals("approved") ? activeStyle : inactiveStyle);
        tabPending.setStyle(tab.equals("pending") ? activeStyle : inactiveStyle);
        tabRejected.setStyle(tab.equals("rejected") ? activeStyle : inactiveStyle);

        List<Question> toShow;
        if (tab.equals("pending")) {
            toShow = pendingQuestions;
        } else if (tab.equals("rejected")) {
            toShow = rejectedQuestions;
        } else {
            toShow = approvedQuestions;
        }

        questionListVBox.getChildren().clear();
        if (toShow.isEmpty()) {
            Label empty = new Label("No questions in this category yet.");
            empty.setStyle("-fx-text-fill: #888888; -fx-font-size: 13;");
            questionListVBox.getChildren().add(empty);
        } else {
            for (Question q : toShow) {
                questionListVBox.getChildren().add(buildQuestionRow(q, tab));
            }
        }
    }

    private HBox buildQuestionRow(Question question, String tab) {
        String rowBg;
        String rowBorder;
        String badgeBg;
        String badgeBorder;
        String badgeText;
        String badgeColor;
        String diffBg;
        String diffColor;

        if (tab.equals("pending")) {
            rowBg = "#fffdf2";
            rowBorder = "#f0e09c";
            badgeBg = "#fff8e1";
            badgeBorder = "#f5d76e";
            badgeText = "◔ Pending";
            badgeColor = "#c68400";
            diffBg = "#fff3cd";
            diffColor = "#856404";
        } else if (tab.equals("rejected")) {
            rowBg = "#fff4f4";
            rowBorder = "#f5bcbc";
            badgeBg = "#ffe4e4";
            badgeBorder = "#f5a0a0";
            badgeText = "⊗ Rejected";
            badgeColor = "#c0392b";
            diffBg = "#ffe4e4";
            diffColor = "#c0392b";
        } else {
            rowBg = "#f2faf4";
            rowBorder = "#b8efc6";
            badgeBg = "#e8f9ee";
            badgeBorder = "#a6dfb4";
            badgeText = "◔ Approved";
            badgeColor = "#28a745";
            diffBg = "#def7e6";
            diffColor = "#28a745";
        }

        HBox row = new HBox(10);
        row.setPrefHeight(66);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: " + rowBg + "; "
          + "-fx-background-radius: 8; "
          + "-fx-border-color: " + rowBorder + "; "
          + "-fx-border-radius: 8;"
        );
        row.setPadding(new Insets(16));

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox titleRow = new HBox(10);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(question.getTitle());
        titleLbl.setStyle("-fx-font-size: 15; -fx-text-fill: #111111;");

        StackPane statusBadge = new StackPane();
        statusBadge.setStyle(
            "-fx-background-color: " + badgeBg + "; "
          + "-fx-background-radius: 6; "
          + "-fx-border-color: " + badgeBorder + "; "
          + "-fx-border-radius: 6; -fx-border-width: 1;"
        );
        statusBadge.setPadding(new Insets(2, 8, 2, 8));

        Label statusLbl = new Label(badgeText);
        statusLbl.setStyle("-fx-font-size: 11; -fx-text-fill: " + badgeColor + ";");
        statusBadge.getChildren().add(statusLbl);

        titleRow.getChildren().addAll(titleLbl, statusBadge);

        String courseStr = question.getCourse() != null
                ? question.getCourse().toString().replace("_", " ") + " • " : "";
        String langStr = question.getLanguage() != null
                ? question.getLanguage().toString() + " • " : "";

        Label subLbl = new Label(courseStr + langStr + "Problem");
        subLbl.setStyle("-fx-font-size: 13; -fx-text-fill: #6d6d6d;");

        info.getChildren().addAll(titleRow, subLbl);

        StackPane diffBadge = new StackPane();
        diffBadge.setStyle("-fx-background-color: " + diffBg + "; -fx-background-radius: 6;");
        diffBadge.setPadding(new Insets(2, 8, 2, 8));

        String diffText = question.getDifficulty() != null
                ? capitalize(question.getDifficulty().toString()) : "N/A";

        Label diffLbl = new Label(diffText);
        diffLbl.setStyle("-fx-font-size: 11; -fx-text-fill: " + diffColor + ";");
        diffBadge.getChildren().add(diffLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        StackPane editBtn = new StackPane();
        editBtn.setStyle(
            "-fx-background-color: #f0f0f0; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        editBtn.setPadding(new Insets(4, 12, 4, 12));
        Label editLbl = new Label("✎ Edit");
        editLbl.setStyle("-fx-font-size: 12; -fx-text-fill: #444444;");
        editBtn.getChildren().add(editLbl);
        editBtn.setOnMouseClicked(e -> handleEdit(question));

        StackPane deleteBtn = new StackPane();
        deleteBtn.setStyle(
            "-fx-background-color: #ffe4e4; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        deleteBtn.setPadding(new Insets(4, 12, 4, 12));
        Label deleteLbl = new Label("🗑 Delete");
        deleteLbl.setStyle("-fx-font-size: 12; -fx-text-fill: #c0392b;");
        deleteBtn.getChildren().add(deleteLbl);
        deleteBtn.setOnMouseClicked(e -> handleDelete(question));

        row.getChildren().addAll(info, spacer, editBtn, deleteBtn, diffBadge);
        return row;
    }

    @FXML
    private void handleAddQuestion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");
        dialog.setHeaderText("Fill in the question details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(16));

        TextField tfTitle = new TextField();
        tfTitle.setPromptText("Title");

        TextField tfPrompt = new TextField();
        tfPrompt.setPromptText("Prompt / description");

        TextField tfDiff = new TextField();
        tfDiff.setPromptText("Difficulty: EASY / MEDIUM / HARD");

        TextField tfLang = new TextField();
        tfLang.setPromptText("Language: JAVA / PYTHON / CPP / JAVASCRIPT / HTML / CSS");

        TextField tfCourse = new TextField();
        tfCourse.setPromptText("Course: CSCE145 / CSCE146 / CSCE240 / CSCE242 / CSCE247");

        form.getChildren().addAll(
            labelFor("Title"), tfTitle,
            labelFor("Prompt"), tfPrompt,
            labelFor("Difficulty"), tfDiff,
            labelFor("Language"), tfLang,
            labelFor("Course"), tfCourse
        );

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            try {
                Difficulty diff = Difficulty.valueOf(tfDiff.getText().trim().toUpperCase());
                Language lang = Language.valueOf(tfLang.getText().trim().toUpperCase());
                Course course = Course.valueOf(tfCourse.getText().trim().toUpperCase());

                facade.addQuestion(
                    tfTitle.getText().trim(),
                    tfPrompt.getText().trim(),
                    diff, lang, course
                );

                loadQuestions();
                updateStatCards();
                renderTab(activeTab);

            } catch (IllegalArgumentException ex) {
                showError("Invalid Input",
                    "Please make sure Difficulty, Language, and Course are valid enum values.\n"
                  + ex.getMessage());
            }
        });
    }

    private void handleEdit(Question question) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit: " + question.getTitle());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(16));

        TextField tfTitle = new TextField(question.getTitle());
        TextField tfPrompt = new TextField(question.getPrompt());

        form.getChildren().addAll(
            labelFor("New Title"), tfTitle,
            labelFor("New Prompt"), tfPrompt
        );
        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) {
                return;
            }

            String newTitle = tfTitle.getText().trim();
            String newPrompt = tfPrompt.getText().trim();

            if (newTitle.isEmpty() || newPrompt.isEmpty()) {
                showError("Validation", "Title and Prompt cannot be empty.");
                return;
            }

            facade.editQuestion(question, newTitle, newPrompt);

            loadQuestions();
            updateStatCards();
            renderTab(activeTab);
        });
    }

    private void handleDelete(Question question) {
        Alert confirm = new Alert(
            AlertType.CONFIRMATION,
            "Delete \"" + question.getTitle() + "\"? This cannot be undone.",
            ButtonType.YES, ButtonType.NO
        );
        confirm.setTitle("Confirm Delete");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                facade.removeQuestion(question);
                loadQuestions();
                updateStatCards();
                renderTab(activeTab);
            }
        });
    }

    private Label labelFor(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        return l;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}