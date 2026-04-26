package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.AccountType;
import com.model.Course;
import com.model.Difficulty;
import com.model.Language;
import com.model.Question;
import com.model.TIPSFacade;
import com.model.User;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddQuestionPopupController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblSubtitle;
    @FXML private Label lblError;

    @FXML private TextField tfQuestionTitle;
    @FXML private TextArea taPrompt;
    @FXML private ComboBox<Difficulty> cbDifficulty;
    @FXML private ComboBox<Language> cbLanguage;
    @FXML private ComboBox<Course> cbCourse;

    @FXML private Button btnCancel;
    @FXML private Button btnCreate;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private Runnable onQuestionCreated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbDifficulty.setItems(FXCollections.observableArrayList(Difficulty.values()));
        cbLanguage.setItems(FXCollections.observableArrayList(Language.values()));
        cbCourse.setItems(FXCollections.observableArrayList(Course.values()));

        cbDifficulty.setPromptText("Select difficulty");
        cbLanguage.setPromptText("Select language");
        cbCourse.setPromptText("Select course");

        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }

        if (taPrompt != null) {
            taPrompt.setWrapText(true);
        }
    }

    public void setOnQuestionCreated(Runnable onQuestionCreated) {
        this.onQuestionCreated = onQuestionCreated;
    }

    @FXML
    private void handleCreateQuestion() {
        User currentUser = facade.getCurrentUser();

        if (currentUser == null) {
            showError("You must be logged in to add a question.");
            return;
        }

        AccountType type = currentUser.getAccountType();
        if (type != AccountType.ADMIN && type != AccountType.EDITOR) {
            showError("Only admins and editors can add questions.");
            return;
        }

        String title = tfQuestionTitle == null ? "" : tfQuestionTitle.getText().trim();
        String prompt = taPrompt == null ? "" : taPrompt.getText().trim();
        Difficulty difficulty = cbDifficulty == null ? null : cbDifficulty.getValue();
        Language language = cbLanguage == null ? null : cbLanguage.getValue();
        Course course = cbCourse == null ? null : cbCourse.getValue();

        if (title.isEmpty()) {
            showError("Please enter a title.");
            return;
        }

        if (prompt.isEmpty()) {
            showError("Please enter a prompt.");
            return;
        }

        if (difficulty == null || language == null || course == null) {
            showError("Please select difficulty, language, and course.");
            return;
        }

        Question created = facade.addQuestion(title, prompt, difficulty, language, course);
        if (created == null) {
            showError("Question could not be created.");
            return;
        }

        if (onQuestionCreated != null) {
            onQuestionCreated.run();
        }

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText("⚠ " + message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btnCreate.getScene().getWindow();
        stage.close();
    }
}