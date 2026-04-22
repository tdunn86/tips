package com.csce247;

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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * Controller for the dashboard content.
 * Pulls all user-facing dashboard data from the facade/model layer.
 */
public class DashboardController implements Initializable {

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
        loadDashboardData();
    }

    private void loadDashboardData() {
        User currentUser = facade.getCurrentUser();
        List<Question> questions = facade.getQuestions(null);

        if (currentUser == null) {
            greetingLabel.setText("Hello, Guest! 👋");
            subtitleLabel.setText("Ready to sharpen your coding skills today?");
            streakValueLabel.setText("0 Days");
            streakLabel.setText("Current Streak - Log in to begin!");
            problemsSolvedLabel.setText("0");
            coursesActiveLabel.setText("0");
            achievementsLabel.setText("0");
            return;
        }

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
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
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
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
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
            if (question.getSolutions() == null) {
                continue;
            }
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

            if (!includeCourse && question.getSolutions() != null) {
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

        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
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

        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            total += student.getFavQuestions().size();
            if (student.getStreak() >= 1) {
                total += 1;
            }
        }

        if (currentUser instanceof Editor) {
            Editor editor = (Editor) currentUser;
            total += editor.totalQuestionsCreated;
            total += countAuthoredQuestions(currentUser, questions);
        } else if (currentUser instanceof Admin) {
            Admin admin = (Admin) currentUser;
            total += admin.totalQuestionsCreated;
            total += countAuthoredQuestions(currentUser, questions);
        }

        return total;
    }

    private int countAcceptedSolutions(User currentUser, List<Question> questions) {
        int count = 0;

        for (Question question : questions) {
            if (question.getSolutions() == null) {
                continue;
            }
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
            if (question.getReplies() == null) {
                continue;
            }
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

        if (reply.getReplies() != null) {
            for (Reply nestedReply : reply.getReplies()) {
                count += countRepliesByUser(currentUser, nestedReply);
            }
        }

        return count;
    }
}