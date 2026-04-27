package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.AccountType;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CreateAccountController implements Initializable {

    @FXML private TextField tfUsername;
    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private ComboBox<String> cbAccountType;
    @FXML private Label lblError;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
        if (cbAccountType != null) {
            cbAccountType.getItems().addAll("Student", "Editor");
            cbAccountType.setValue("Student");
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

        AccountType accountType;
        String selected = cbAccountType.getValue();

        if ("Editor".equalsIgnoreCase(selected)) {
            accountType = AccountType.EDITOR;
        } else {
            accountType = AccountType.STUDENT;
        }

        try {
            facade.registerUser(username, password, email, accountType);
            showInfo("Account created successfully. You can now log in.");

            goBackToLogin();

        } catch (Exception e) {
            showError("Could not create account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToLogin() {
        goBackToLogin();
    }

    private void goBackToLogin() {
        MainController main = getMainController();
        if (main != null) {
            main.showAuthPage("login.fxml");
        }
    }

    private MainController getMainController() {
        if (tfUsername == null || tfUsername.getScene() == null) return null;

        Object controller = tfUsername.getScene().getRoot().getUserData();
        if (controller instanceof MainController) {
            return (MainController) controller;
        }
        return null;
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