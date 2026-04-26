package com.csce247;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the Contributor Dashboard (contributor.fxml).
 *
 * Supports both Editor and Admin account types.
 */
public class ContributorController implements Initializable {

    @FXML private Label lblUsername;

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

    @FXML private StackPane btnAddQuestion;

    private TIPSFacade facade;
    private User currentUser;

    private String activeTab = "approved";

    private final List<Question> approvedQuestions = new ArrayList<>();
    private final List<Question> pendingQuestions = new ArrayList<>();
    private final List<Question> rejectedQuestions = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        facade = TIPSFacade.getInstance();
        currentUser = facade.getCurrentUser();

        if (currentUser == null ||
            currentUser.getAccountType() == com.model.AccountType.STUDENT) {
            javafx.application.Platform.runLater(() -> {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/dashboard.fxml"));
                    Stage stage = (Stage) questionListVBox.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return;
        }

        if (btnAddQuestion != null) {
            btnAddQuestion.setOnMouseClicked(event -> openAddQuestionPopup());
        }

        populateUserInfo();
        refreshView();
    }

    private void refreshView() {
        loadQuestions();
        updateStatCards();
        renderTab(activeTab);
    }

    private void populateUserInfo() {
        if (currentUser != null && lblUsername != null) {
            lblUsername.setText(currentUser.getUsername());
        }
    }

    private void loadQuestions() {
        approvedQuestions.clear();
        pendingQuestions.clear();
        rejectedQuestions.clear();

        if (currentUser == null) return;

        ArrayList<Question> all = facade.getQuestions("");
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
        if (currentUser == null) return 0;

        ArrayList<Question> all = facade.getQuestions("");
        int count = 0;

        for (Question q : all) {
            if (q.getSolutions() == null) continue;
            for (var s : q.getSolutions()) {
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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(info, spacer, editBtn, deleteBtn, diffBadge);
        return row;
    }

    @FXML
    private void handleAddQuestion() {
        openAddQuestionPopup();
    }

    private void openAddQuestionPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/csce247/addQuestionPopup.fxml"));
            Parent root = loader.load();

            AddQuestionPopupController controller = loader.getController();
            controller.setOnQuestionCreated(this::refreshView);

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            if (questionListVBox != null && questionListVBox.getScene() != null) {
                popupStage.initOwner(questionListVBox.getScene().getWindow());
            }
            popupStage.setTitle("Create Question");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.showAndWait();
        } catch (Exception e) {
            System.err.println("Failed to load addQuestionPopup.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEdit(Question question) {
        Alert alert = new Alert(AlertType.INFORMATION, "Edit flow kept as-is for now. Reconnect your existing edit UI here.", ButtonType.OK);
        alert.setTitle("Edit Question");
        alert.showAndWait();
    }

    private void handleDelete(Question question) {
        Alert confirm = new Alert(AlertType.CONFIRMATION,
            "Delete \"" + question.getTitle() + "\"? This cannot be undone.",
            ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                facade.removeQuestion(question);
                refreshView();
            }
        });
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        navigate(event, "dashboard.fxml");
    }

    @FXML
    private void goQuestions(ActionEvent event) {
        navigate(event, "question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) {
        navigate(event, "dailyChallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) {
        navigate(event, "contributor.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        facade.logout();
        navigate(event, "login.fxml");
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    private void navigate(ActionEvent event, String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Navigation failed: " + fxmlFile + " — " + e.getMessage());
            e.printStackTrace();
        }
    }
}