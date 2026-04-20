package com.csce247;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.model.Course;
import com.model.Difficulty;
import com.model.Language;
import com.model.Question;
import com.model.TIPSFacade;
import com.model.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
 
/**
 * Controller for the Contributor Dashboard (contributor.fxml).
 *
 * Supports both Editor and Admin account types (both have createQuestion /
 * editQuestion / deleteQuestion).  All data flows through TIPSFacade so this
 * controller never touches persistence directly.
 */
public class ContributorController implements Initializable {
 
    // ------------------------------------------------------------------ //
    //  FXML-injected nodes                                                //
    // ------------------------------------------------------------------ //
 
    /** Top-bar username label */
    @FXML private Label lblUsername;
 
    // Stat cards
    @FXML private Label lblProblemsSubmitted;
    @FXML private Label lblSolutionsWritten;
    @FXML private Label lblApprovalRate;
 
    // Tab bar panes (used to toggle active/inactive styling)
    @FXML private StackPane tabApproved;
    @FXML private StackPane tabPending;
    @FXML private StackPane tabRejected;
 
    // Tab labels (so we can update the counts in the text)
    @FXML private Label lblTabApproved;
    @FXML private Label lblTabPending;
    @FXML private Label lblTabRejected;
 
    // The scrollable list area – we fill this dynamically
    @FXML private VBox questionListVBox;
 
    // "Add Question" button (defined in the FXML top-right area)
    @FXML private StackPane btnAddQuestion;
 
    // ------------------------------------------------------------------ //
    //  Internal state                                                      //
    // ------------------------------------------------------------------ //
 
    private TIPSFacade facade;
    private User currentUser;
 
    /** Which tab is currently selected: "approved", "pending", "rejected" */
    private String activeTab = "approved";
 
    // We maintain three local lists.  In a full implementation these would
    // come from the server / DataLoader; here we derive them from the
    // question list the facade exposes.
    private List<Question> approvedQuestions  = new ArrayList<>();
    private List<Question> pendingQuestions   = new ArrayList<>();
    private List<Question> rejectedQuestions  = new ArrayList<>();
 
    // ------------------------------------------------------------------ //
    //  Initializable                                                       //
    // ------------------------------------------------------------------ //
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        facade      = TIPSFacade.getInstance();
        currentUser = facade.getCurrentUser();
 
        populateUserInfo();
        loadQuestions();
        updateStatCards();
        renderTab(activeTab);
    }
 
    // ------------------------------------------------------------------ //
    //  Data loading helpers                                               //
    // ------------------------------------------------------------------ //
 
    /** Display the logged-in user's name in the top bar. */
    private void populateUserInfo() {
        if (currentUser != null) {
            lblUsername.setText(currentUser.getUsername());
        }
    }
 
    /**
     * Pulls questions from the facade and bins them into approved / pending /
     * rejected lists based on the current user being the author.
     *
     * Because the UML does not define an explicit "status" field on Question
     * (approval is an admin workflow), we use the following heuristic that
     * fits the existing model:
     *
     *   • A question whose isSolutionRevealed == true  → Approved
     *     (an Admin/Editor revealing the solution signals final approval)
     *   • A question whose solutions list is empty     → Pending Review
     *     (no solution attached yet; still under review)
     *   • A question whose solutions list is non-empty
     *     but isSolutionRevealed == false              → Rejected
     *     (solution submitted but not accepted)
     *
     * Teams can swap this logic for a dedicated status enum without touching
     * the controller structure.
     */
    private void loadQuestions() {
        approvedQuestions.clear();
        pendingQuestions.clear();
        rejectedQuestions.clear();
 
        if (currentUser == null) return;
 
        ArrayList<Question> all = facade.getQuestions("");   // "" = no filter
        for (Question q : all) {
            if (q.getAuthor() == null) continue;
            if (q.getAuthor().getUserId() != currentUser.getUserId()) continue;
 
            if (q.isSolutionRevealed()) {
                approvedQuestions.add(q);
            } else if (q.getSolutions() == null || q.getSolutions().isEmpty()) {
                pendingQuestions.add(q);
            } else {
                rejectedQuestions.add(q);
            }
        }
    }
 
    // ------------------------------------------------------------------ //
    //  Stat cards                                                          //
    // ------------------------------------------------------------------ //
 
    private void updateStatCards() {
        int totalSubmitted = approvedQuestions.size()
                           + pendingQuestions.size()
                           + rejectedQuestions.size();
 
        // "Solutions written" = questions that have at least one solution
        // authored by the current user across all question entries.
        int solutionsWritten = countSolutionsWritten();
 
        int approvalRate = totalSubmitted == 0
                ? 0
                : (int) Math.round((approvedQuestions.size() * 100.0) / totalSubmitted);
 
        lblProblemsSubmitted.setText(String.valueOf(totalSubmitted));
        lblSolutionsWritten.setText(String.valueOf(solutionsWritten));
        lblApprovalRate.setText(approvalRate + "%");
    }
 
    private int countSolutionsWritten() {
        if (currentUser == null) return 0;
        ArrayList<Question> all = facade.getQuestions("");
        int count = 0;
        for (Question q : all) {
            if (q.getSolutions() == null) continue;
            for (var s : q.getSolutions()) {
                if (s.getAuthor() != null
                        && s.getAuthor().getUserId() == currentUser.getUserId()) {
                    count++;
                }
            }
        }
        return count;
    }
 
    // ------------------------------------------------------------------ //
    //  Tab switching                                                       //
    // ------------------------------------------------------------------ //
 
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
        // Update tab label counts
        lblTabApproved.setText("◔  Approved (" + approvedQuestions.size() + ")");
        lblTabPending.setText("◔  Pending Review (" + pendingQuestions.size() + ")");
        lblTabRejected.setText("⊗  Rejected (" + rejectedQuestions.size() + ")");
 
        // Highlight active tab
        String activeStyle   = "-fx-background-color: white; -fx-background-radius: 12; "
                             + "-fx-border-color: #d0d0d0; -fx-border-radius: 12; -fx-border-width: 1;";
        String inactiveStyle = "-fx-background-color: transparent;";
 
        tabApproved.setStyle(tab.equals("approved") ? activeStyle : inactiveStyle);
        tabPending .setStyle(tab.equals("pending")  ? activeStyle : inactiveStyle);
        tabRejected.setStyle(tab.equals("rejected") ? activeStyle : inactiveStyle);
 
        // Render the correct list
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
 
    // ------------------------------------------------------------------ //
    //  Dynamic question row builder                                        //
    // ------------------------------------------------------------------ //
 
    /**
     * Builds a styled HBox row for one question, matching the visual design
     * already established in the static FXML prototype.
     */
    private HBox buildQuestionRow(Question question, String tab) {
        // ---- colour scheme per tab ------------------------------------ //
        String rowBg, rowBorder, badgeBg, badgeBorder, badgeText, badgeColor;
        String diffBg, diffColor;
 
        if (tab.equals("pending")) {
            rowBg = "#fffdf2"; rowBorder = "#f0e09c";
            badgeBg = "#fff8e1"; badgeBorder = "#f5d76e"; badgeText = "◔ Pending";
            badgeColor = "#c68400";
            diffBg = "#fff3cd"; diffColor = "#856404";
        } else if (tab.equals("rejected")) {
            rowBg = "#fff4f4"; rowBorder = "#f5bcbc";
            badgeBg = "#ffe4e4"; badgeBorder = "#f5a0a0"; badgeText = "⊗ Rejected";
            badgeColor = "#c0392b";
            diffBg = "#ffe4e4"; diffColor = "#c0392b";
        } else {
            rowBg = "#f2faf4"; rowBorder = "#b8efc6";
            badgeBg = "#e8f9ee"; badgeBorder = "#a6dfb4"; badgeText = "◔ Approved";
            badgeColor = "#28a745";
            diffBg = "#def7e6"; diffColor = "#28a745";
        }
 
        // ---- Main row ------------------------------------------------- //
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
 
        // ---- Info VBox ------------------------------------------------ //
        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);
 
        // Title + status badge
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
 
        // Sub-line: course • type • submitted date
        String courseStr = question.getCourse() != null
                ? question.getCourse().toString().replace("_", " ") + " • " : "";
        String langStr   = question.getLanguage() != null
                ? question.getLanguage().toString() + " • " : "";
        // Question objects don't store a submission date in the UML, so we
        // show language instead, which is always available.
        Label subLbl = new Label(courseStr + langStr + "Problem");
        subLbl.setStyle("-fx-font-size: 13; -fx-text-fill: #6d6d6d;");
 
        info.getChildren().addAll(titleRow, subLbl);
 
        // ---- Difficulty badge ----------------------------------------- //
        StackPane diffBadge = new StackPane();
        diffBadge.setStyle("-fx-background-color: " + diffBg + "; -fx-background-radius: 6;");
        diffBadge.setPadding(new Insets(2, 8, 2, 8));
        String diffText = question.getDifficulty() != null
                ? capitalize(question.getDifficulty().toString()) : "N/A";
        Label diffLbl = new Label(diffText);
        diffLbl.setStyle("-fx-font-size: 11; -fx-text-fill: " + diffColor + ";");
        diffBadge.getChildren().add(diffLbl);
 
        // ---- Edit button --------------------------------------------- //
        StackPane editBtn = new StackPane();
        editBtn.setStyle(
            "-fx-background-color: #f0f0f0; -fx-background-radius: 6; "
          + "-fx-cursor: hand;"
        );
        editBtn.setPadding(new Insets(4, 12, 4, 12));
        Label editLbl = new Label("✎ Edit");
        editLbl.setStyle("-fx-font-size: 12; -fx-text-fill: #444444;");
        editBtn.getChildren().add(editLbl);
        editBtn.setOnMouseClicked(e -> handleEdit(question));
 
        // ---- Delete button ------------------------------------------- //
        StackPane deleteBtn = new StackPane();
        deleteBtn.setStyle(
            "-fx-background-color: #ffe4e4; -fx-background-radius: 6; "
          + "-fx-cursor: hand;"
        );
        deleteBtn.setPadding(new Insets(4, 12, 4, 12));
        Label deleteLbl = new Label("🗑 Delete");
        deleteLbl.setStyle("-fx-font-size: 12; -fx-text-fill: #c0392b;");
        deleteBtn.getChildren().add(deleteLbl);
        deleteBtn.setOnMouseClicked(e -> handleDelete(question));
 
        // ---- Spacer --------------------------------------------------- //
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
 
        row.getChildren().addAll(info, spacer, editBtn, deleteBtn, diffBadge);
        return row;
    }
 
    // ------------------------------------------------------------------ //
    //  Add Question                                                        //
    // ------------------------------------------------------------------ //
 
    /**
     * Opens a simple dialog to collect title, prompt, difficulty, language,
     * and course, then delegates to the facade.
     */
    @FXML
    private void handleAddQuestion() {
        // Build a simple input dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");
        dialog.setHeaderText("Fill in the question details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        VBox form = new VBox(10);
        form.setPadding(new Insets(16));
 
        TextField tfTitle    = new TextField(); tfTitle.setPromptText("Title");
        TextField tfPrompt   = new TextField(); tfPrompt.setPromptText("Prompt / description");
 
        // Simple combo-style inputs using TextFields with hint; a real
        // implementation would use ComboBox<Difficulty> etc.
        TextField tfDiff     = new TextField(); tfDiff.setPromptText("Difficulty: EASY / MEDIUM / HARD");
        TextField tfLang     = new TextField(); tfLang.setPromptText("Language: JAVA / PYTHON / CPP / JAVASCRIPT / HTML / CSS");
        TextField tfCourse   = new TextField(); tfCourse.setPromptText("Course: CSCE145 / CSCE146 / CSCE240 / CSCE242 / CSCE247");
 
        form.getChildren().addAll(
            labelFor("Title"),   tfTitle,
            labelFor("Prompt"),  tfPrompt,
            labelFor("Difficulty"), tfDiff,
            labelFor("Language"),   tfLang,
            labelFor("Course"),     tfCourse
        );
 
        dialog.getDialogPane().setContent(form);
 
        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
 
            try {
                Difficulty diff   = Difficulty.valueOf(tfDiff.getText().trim().toUpperCase());
                Language   lang   = Language.valueOf(tfLang.getText().trim().toUpperCase());
                Course     course = Course.valueOf(tfCourse.getText().trim().toUpperCase());
 
                facade.addQuestion(
                    tfTitle.getText().trim(),
                    tfPrompt.getText().trim(),
                    diff, lang, course
                );
 
                // Reload & re-render
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
 
    // ------------------------------------------------------------------ //
    //  Edit Question                                                       //
    // ------------------------------------------------------------------ //
 
    private void handleEdit(Question question) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit: " + question.getTitle());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
 
        VBox form = new VBox(10);
        form.setPadding(new Insets(16));
 
        TextField tfTitle  = new TextField(question.getTitle());
        TextField tfPrompt = new TextField(question.getPrompt());
 
        form.getChildren().addAll(
            labelFor("New Title"),  tfTitle,
            labelFor("New Prompt"), tfPrompt
        );
        dialog.getDialogPane().setContent(form);
 
        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;
 
            String newTitle  = tfTitle.getText().trim();
            String newPrompt = tfPrompt.getText().trim();
 
            if (newTitle.isEmpty() || newPrompt.isEmpty()) {
                showError("Validation", "Title and Prompt cannot be empty.");
                return;
            }
 
            // TIPSFacade.editQuestion updates title and prompt
            facade.editQuestion(question, newTitle, newPrompt);
 
            // Reload
            loadQuestions();
            updateStatCards();
            renderTab(activeTab);
        });
    }
 
    // ------------------------------------------------------------------ //
    //  Delete Question                                                     //
    // ------------------------------------------------------------------ //
 
    private void handleDelete(Question question) {
        Alert confirm = new Alert(AlertType.CONFIRMATION,
            "Delete \"" + question.getTitle() + "\"? This cannot be undone.",
            ButtonType.YES, ButtonType.NO);
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
 
    // ------------------------------------------------------------------ //
    //  Navigation handlers                                                 //
    // ------------------------------------------------------------------ //
 
    @FXML
    private void handleNavHome() {
        navigateTo("home.fxml");
    }
 
    @FXML
    private void handleNavQuestions() {
        navigateTo("questions.fxml");
    }
 
    @FXML
    private void handleNavDailyChallenge() {
        navigateTo("dailyChallenge.fxml");
    }
 
    @FXML
    private void handleNavLogout() {
        facade.logout();
        navigateTo("login.fxml");
    }
 
    // ------------------------------------------------------------------ //
    //  Utility helpers                                                     //
    // ------------------------------------------------------------------ //
 
    private Label labelFor(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        return l;
    }
 
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }
 
    private void showError(String title, String msg) {
        Alert alert = new Alert(AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
 
    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ui/" + fxmlFile));
            Stage stage = (Stage) lblUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            // If the target FXML doesn't exist yet, silently ignore for now.
            System.err.println("Navigation failed: " + fxmlFile + " — " + e.getMessage());
        }
    }
}