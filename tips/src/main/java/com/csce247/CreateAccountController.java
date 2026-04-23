package com.csce247;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.model.AccountType;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateAccountController implements Initializable {

    @FXML private TextField tfUsername;
    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private RadioButton rbStudent;
    @FXML private RadioButton rbEditor;
    @FXML private Label lblError;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }

        if (rbStudent != null) {
            rbStudent.setSelected(true);
        }
    }

    @FXML
    private void handleCreateAccount() {
        String username = tfUsername.getText() == null ? "" : tfUsername.getText().trim();
        String email = tfEmail.getText() == null ? "" : tfEmail.getText().trim();
        String password = pfPassword.getText() == null ? "" : pfPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        AccountType accountType = rbEditor.isSelected() ? AccountType.EDITOR : AccountType.STUDENT;

        try {
            facade.registerUser(username, password, email, accountType);
            showInfo("Account created successfully. You can now log in.");
            navigateToLogin();
        } catch (Exception e) {
            showError("Could not create account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() throws IOException {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/login.fxml"));
            Stage stage = (Stage) tfUsername.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load login.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Account Created");
        alert.setContentText(message);
        alert.showAndWait();
    }
}