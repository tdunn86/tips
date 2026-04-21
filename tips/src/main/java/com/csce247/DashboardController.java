package com.csce247;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import com.model.Admin;
import com.model.Editor;
import com.model.Question;
import com.model.Reply;
import com.model.Solution;
import com.model.Student;
import com.model.TIPSFacade;
import com.model.User;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Controller for the dashboard view.
 * Pulls all user-facing dashboard data from the facade/model layer.
 */
public class DashboardController implements Initializable {

    @FXML private BorderPane rootPane;

    @FXML private Button logoButton;
    @FXML private Button homeNavButton;
    @FXML private Button questionsNavButton;
    @FXML private Button dailyChallengeNavButton;
    @FXML private Button contributorNavButton;
    @FXML private Button profileButton;
    @FXML private Button exploreQuestionsButton;
    @FXML private Button startChallengeButton;
    @FXML private Button createQuestionButton;

    @FXML private Label usernameLabel;
    @FXML private Label greetingLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label streakValueLabel;
    @FXML private Label streakLabel;
    @FXML private Label problemsSolvedLabel;
    @FXML private Label coursesActiveLabel;
    @FXML private Label achievementsLabel;

    private final TIPSFacade facade = TIPSFacade.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setActiveNav(homeNavButton);
        loadDashboardData();
    }

    private void loadDashboardData() {
        User currentUser = facade.getCurrentUser();
        List<Question> questions = facade.getQuestions(null);

        if (currentUser == null) {
            usernameLabel.setText("Guest");
            greetingLabel.setText("Hello, Guest! 👋");
            subtitleLabel.setText("Ready to sharpen your coding skills today?");
            streakValueLabel.setText("0 Days");
            streakLabel.setText("Current Streak - Log in to begin!");
            problemsSolvedLabel.setText("0");
            coursesActiveLabel.setText("0");
            achievementsLabel.setText("0");
            return;
        }

        usernameLabel.setText(currentUser.getUsername());
        greetingLabel.setText("Hello, " + currentUser.getUsername() + "! 👋");
        subtitleLabel.setText(buildSubtitle(currentUser));

        int streak = getDisplayedStreak(currentUser);
        streakValueLabel.setText(streak + (streak == 1 ? " Day" : " Days"));
        streakLabel.setText(buildStreakMessage(currentUser, streak));

        problemsSolvedLabel.setText(String.valueOf(countSolvedQuestions(currentUser, questions)));
        coursesActiveLabel.setText(String.valueOf(countActiveCourses(currentUser, questions)));
        achievementsLabel.setText(String.valueOf(countAchievements(currentUser, questions)));
    }

    private String buildSubtitle(User currentUser) {
        if (currentUser instanceof Student student) {
            return "Classification: " + student.getClassification() + " • Ready to sharpen your coding skills today?";
        }
        if (currentUser instanceof Editor) {
            return "Create and manage coding problems for students to practice.";
        }
        if (currentUser instanceof Admin) {
            return "Manage questions, contributors, and platform activity from your dashboard.";
        }
        return "Ready to sharpen your coding skills today?";
    }

    private int getDisplayedStreak(User currentUser) {
        if (currentUser instanceof Student student) {
            return student.getStreak();
        }
        return 0;
    }

    private String buildStreakMessage(User currentUser, int streak) {
        if (currentUser instanceof Student) {
            return streak > 0 ? "Current Streak - Keep it up!" : "Current Streak - Start today!";
        }
        if (currentUser instanceof Editor) {
            return "Editor account - streak tracking applies to students.";
        }
        if (currentUser instanceof Admin) {
            return "Admin account - streak tracking applies to students.";
        }
        return "Current Streak";
    }

    private int countSolvedQuestions(User currentUser, List<Question> questions) {
        Set<String> solvedQuestionIds = new HashSet<>();

        for (Question question : questions) {
            for (Solution solution : question.getSolutions()) {
                if (solution.getAuthor() != null
                        && solution.getAuthor().getUserId() == currentUser.getUserId()) {
                    solvedQuestionIds.add(question.getQuestionID().toString());
                    break;
                }
            }
        }

        return solvedQuestionIds.size();
    }

    private int countActiveCourses(User currentUser, List<Question> questions) {
        Set<String> activeCourses = new HashSet<>();

        for (Question question : questions) {
            boolean includeCourse = false;

            if (question.getAuthor() != null
                    && question.getAuthor().getUserId() == currentUser.getUserId()) {
                includeCourse = true;
            }

            if (!includeCourse) {
                for (Solution solution : question.getSolutions()) {
                    if (solution.getAuthor() != null
                            && solution.getAuthor().getUserId() == currentUser.getUserId()) {
                        includeCourse = true;
                        break;
                    }
                }
            }

            if (includeCourse && question.getCourse() != null) {
                activeCourses.add(question.getCourse().name());
            }
        }

        if (currentUser instanceof Student student) {
            for (Question favorite : student.getFavQuestions()) {
                if (favorite != null && favorite.getCourse() != null) {
                    activeCourses.add(favorite.getCourse().name());
                }
            }
        }

        return activeCourses.size();
    }

    private int countAchievements(User currentUser, List<Question> questions) {
        int total = 0;

        total += countAcceptedSolutions(currentUser, questions);
        total += countAuthoredComments(currentUser, questions);

        if (currentUser instanceof Student student) {
            total += student.getFavQuestions().size();
            if (student.getStreak() >= 1) {
                total += 1;
            }
        }

        if (currentUser instanceof Editor editor) {
            total += editor.totalQuestionsCreated;
            total += countAuthoredQuestions(currentUser, questions);
        } else if (currentUser instanceof Admin admin) {
            total += admin.totalQuestionsCreated;
            total += countAuthoredQuestions(currentUser, questions);
        }

        return total;
    }

    private int countAcceptedSolutions(User currentUser, List<Question> questions) {
        int count = 0;

        for (Question question : questions) {
            for (Solution solution : question.getSolutions()) {
                if (solution.getAuthor() != null
                        && solution.getAuthor().getUserId() == currentUser.getUserId()
                        && solution.isAccepted()) {
                    count++;
                }
            }
        }

        return count;
    }

    private int countAuthoredQuestions(User currentUser, List<Question> questions) {
        int count = 0;

        for (Question question : questions) {
            if (question.getAuthor() != null
                    && question.getAuthor().getUserId() == currentUser.getUserId()) {
                count++;
            }
        }

        return count;
    }

    private int countAuthoredComments(User currentUser, List<Question> questions) {
        int count = 0;

        for (Question question : questions) {
            for (Reply reply : question.getReplies()) {
                count += countRepliesByUser(currentUser, reply);
            }
        }

        return count;
    }

    private int countRepliesByUser(User currentUser, Reply reply) {
        int count = 0;

        if (reply.getAuthor() != null && reply.getAuthor().getUserId() == currentUser.getUserId()) {
            count++;
        }

        for (Reply nestedReply : reply.getReplies()) {
            count += countRepliesByUser(currentUser, nestedReply);
        }

        return count;
    }

    private void setActiveNav(Button activeButton) {
        Button[] buttons = {homeNavButton, questionsNavButton, dailyChallengeNavButton, contributorNavButton};
        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
            }
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void goDashboard(ActionEvent event) throws IOException {
        setActiveNav(homeNavButton);
        navigate(event, "dashboard.fxml");
    }

    @FXML
    private void goQuestions(ActionEvent event) throws IOException {
        setActiveNav(questionsNavButton);
        navigate(event, "question.fxml");
    }

    @FXML
    private void goDailyChallenge(ActionEvent event) throws IOException {
        setActiveNav(dailyChallengeNavButton);
        navigate(event, "dailychallenge.fxml");
    }

    @FXML
    private void goContributor(ActionEvent event) throws IOException {
        setActiveNav(contributorNavButton);
        navigate(event, "contributor.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        facade.logout();
        navigate(event, "login.fxml");
    }

    private void navigate(ActionEvent event, String fxmlName) throws IOException {
        URL resource = resolveViewResource(fxmlName);
        if (resource == null) {
            showError("Missing view", "Could not find " + fxmlName + " on the classpath.");
            return;
        }

        Parent root = FXMLLoader.load(resource);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene currentScene = stage.getScene();
        Scene nextScene = new Scene(
                root,
                currentScene != null ? currentScene.getWidth() : 1600,
                currentScene != null ? currentScene.getHeight() : 900
        );
        stage.setScene(nextScene);
        stage.show();
    }

    private URL resolveViewResource(String fxmlName) {
        String[] candidates = {
            "/com/csce247/" + fxmlName,
            "/view/" + fxmlName,
            "/views/" + fxmlName,
            "/com/view/" + fxmlName,
            "/com/views/" + fxmlName
        };

        for (String candidate : candidates) {
            URL resource = getClass().getResource(candidate);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
