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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/csce247/createAccount.fxml"));
            Stage stage = (Stage) tfUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.err.println("Failed to load createAccount.fxml: " + e.getMessage());
            e.printStackTrace();
        }
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