package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.AccountType;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private Label lblError;

    private TIPSFacade facade;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        facade = TIPSFacade.getInstance();
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
    }

    @FXML
    private void handleLogin() {
        String username = tfUsername.getText() == null ? "" : tfUsername.getText().trim();
        String password = pfPassword.getText() == null ? "" : pfPassword.getText();

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

        AccountType type = facade.getCurrentUser().getAccountType();

        String firstPage;
        switch (type) {
            case ADMIN:
            case EDITOR:
                firstPage = "contributor.fxml";
                break;
            case STUDENT:
            default:
                firstPage = "dashboard.fxml";
                break;
        }

        MainController main = getMainController();
        if (main != null) {
            main.showPage(firstPage);
        }
    }

    @FXML
    private void handleCreateAccount() {
        MainController main = getMainController();
        if (main != null) {
            main.showAuthPage("createaccount.fxml");
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
            lblError.setText("⚠  " + message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        } else {
            Alert alert = new Alert(AlertType.ERROR, message);
            alert.setTitle("Error");
            alert.showAndWait();
        }
    }
}