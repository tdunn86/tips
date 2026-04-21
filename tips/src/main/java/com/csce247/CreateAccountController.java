package com.csce247;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class CreateAccountController {

    @FXML private TextField tfUsername;
    @FXML private TextField tfEmail;
    @FXML private PasswordField pfPassword;
    @FXML private RadioButton rbStudent;
    @FXML private RadioButton rbEditor;
    @FXML private Label lblError;

    @FXML
    private void handleCreateAccount() {
        String username = tfUsername.getText().trim();
        String email    = tfEmail.getText().trim();
        String password = pfPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            lblError.setText("Please fill in all fields.");
            lblError.setVisible(true);
            return;
        }

        lblError.setVisible(false);
        String accountType = rbStudent.isSelected() ? "student" : "editor";

        // TODO: save the new user, then switch to login or dashboard
        System.out.println("Creating account: " + username + " / " + accountType);
    }

    @FXML
    private void handleBackToLogin() {
        // TODO: navigate back to login.fxml
        System.out.println("Back to login");
    }
}