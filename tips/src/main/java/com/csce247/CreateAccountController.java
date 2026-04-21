package com.csce247;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

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
    private void handleBackToLogin() throws IOException {
        App.setRoot("contributor");
    }
}