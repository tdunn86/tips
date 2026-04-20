package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.AccountType;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for login.fxml.
 * Handles login and account creation via TIPSFacade.
 */
public class LoginController implements Initializable {

    // ------------------------------------------------------------------ //
    //  FXML-injected nodes                                                //
    // ------------------------------------------------------------------ //

    @FXML private TextField     tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Label         lblError;

    // ------------------------------------------------------------------ //
    //  Internal state                                                      //
    // ------------------------------------------------------------------ //

    private TIPSFacade facade;

    // ------------------------------------------------------------------ //
    //  Initializable                                                       //
    // ------------------------------------------------------------------ //

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        facade = TIPSFacade.getInstance();
        lblError.setVisible(false);
    }

    // ------------------------------------------------------------------ //
    //  Login                                                               //
    // ------------------------------------------------------------------ //

    /**
     * Called when the Login button is clicked.
     * Delegates to TIPSFacade.login(username, password).
     * On success, navigates to the appropriate dashboard based on account type.
     */
    @FXML
    private void handleLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both a username and password.");
            return;
        }

        boolean success = facade.login(username, password);

        if (!success) {
            showError("Invalid username or password. Please try again.");
            pfPassword.clear();
            return;
        }

        // Navigate based on account type
        AccountType type = facade.getCurrentUser().getAccountType();
        String fxmlTarget;
        switch (type) {
            case ADMIN:
            case EDITOR:
                fxmlTarget = "contributor.fxml";
                break;
            case STUDENT:
            default:
                fxmlTarget = "dashboard.fxml";
                break;
        }

        navigateTo(fxmlTarget);
    }

    // ------------------------------------------------------------------ //
    //  Create Account                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Called when the "Create Account" hyperlink is clicked.
     * Opens a dialog to collect username, password, email, and account type,
     * then calls TIPSFacade.registerUser().
     */
    @FXML
    private void handleCreateAccount() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Account");
        dialog.setHeaderText("Fill in your details to register");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox form = new VBox(10);
        form.setPadding(new Insets(16));

        TextField     tfNewUsername = new TextField();
        tfNewUsername.setPromptText("Username");

        PasswordField pfNewPassword = new PasswordField();
        pfNewPassword.setPromptText("Password");

        PasswordField pfConfirm = new PasswordField();
        pfConfirm.setPromptText("Confirm Password");

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email address");

        TextField tfType = new TextField();
        tfType.setPromptText("Account type: STUDENT / EDITOR / ADMIN");

        Label lblFormError = new Label();
        lblFormError.setStyle("-fx-text-fill: #c0392b; -fx-font-size: 11;");
        lblFormError.setVisible(false);

        form.getChildren().addAll(
            bold("Username"),    tfNewUsername,
            bold("Password"),    pfNewPassword,
            bold("Confirm Password"), pfConfirm,
            bold("Email"),       tfEmail,
            bold("Account Type"), tfType,
            lblFormError
        );

        dialog.getDialogPane().setContent(form);

        dialog.showAndWait().ifPresent(result -> {
            if (result != ButtonType.OK) return;

            String newUser  = tfNewUsername.getText().trim();
            String newPass  = pfNewPassword.getText();
            String confirm  = pfConfirm.getText();
            String email    = tfEmail.getText().trim();
            String typeStr  = tfType.getText().trim().toUpperCase();

            // Basic validation
            if (newUser.isEmpty() || newPass.isEmpty() || email.isEmpty() || typeStr.isEmpty()) {
                showError("All fields are required.");
                return;
            }
            if (!newPass.equals(confirm)) {
                showError("Passwords do not match.");
                return;
            }

            AccountType accountType;
            try {
                accountType = AccountType.valueOf(typeStr);
            } catch (IllegalArgumentException e) {
                showError("Account type must be STUDENT, EDITOR, or ADMIN.");
                return;
            }

            // Delegate to facade — registerUser handles duplicate checks internally
            try {
                facade.registerUser(newUser, newPass, email, accountType);
                showInfo("Account created! You can now log in.");
            } catch (Exception e) {
                showError("Could not create account: " + e.getMessage());
            }
        });
    }

    // ------------------------------------------------------------------ //
    //  Utility helpers                                                     //
    // ------------------------------------------------------------------ //

    private void showError(String message) {
        lblError.setText("⚠  " + message);
        lblError.setVisible(true);
    }

    private void showInfo(String message) {
        Alert alert = new Alert(AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle("Success");
        alert.showAndWait();
    }

    private Label bold(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        return l;
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/" + fxmlFile));
            Stage stage = (Stage) tfUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Navigation failed: " + fxmlFile + " — " + e.getMessage());
        }
    }
}