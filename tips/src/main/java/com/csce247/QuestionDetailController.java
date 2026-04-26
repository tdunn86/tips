package com.csce247;

import java.net.URL;
import java.util.ResourceBundle;

import com.model.Question;
import com.model.Reply;
import com.model.TIPSFacade;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class QuestionDetailController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblDifficulty;
    @FXML private Label lblLanguage;
    @FXML private Label lblCourse;
    @FXML private Label lblPrompt;

    @FXML private VBox hintBox;
    @FXML private Label lblHint;
    @FXML private Button btnToggleHint;

    @FXML private VBox solutionBox;
    @FXML private Label lblSampleSolution;
    @FXML private Label lblSampleExplanation;
    @FXML private Button btnViewSolution;

    @FXML private VBox repliesContainer;
    @FXML private TextArea replyInput;

    private final TIPSFacade facade = TIPSFacade.getInstance();
    private Question currentQuestion;

    private boolean hintVisible = false;
    private boolean solutionVisible = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hintBox.setVisible(false);
        hintBox.setManaged(false);
        solutionBox.setVisible(false);
        solutionBox.setManaged(false);
    }

    public void setQuestion(Question q) {
        this.currentQuestion = q;

        if (q == null) return;

        lblTitle.setText(q.getTitle());
        lblDifficulty.setText(q.getDifficulty().toString());
        lblLanguage.setText(q.getLanguage().toString());
        lblCourse.setText(q.getCourse().toString());
        lblPrompt.setText(q.getPrompt());

        // Hint
        if (q.getHint() != null && !q.getHint().isBlank()) {
            hintBox.setVisible(true);
            hintBox.setManaged(true);
            lblHint.setText(q.getHint());
        }

        // Solution
        if (q.getSampleSolution() != null && !q.getSampleSolution().isBlank()) {
            solutionBox.setVisible(true);
            solutionBox.setManaged(true);
            lblSampleSolution.setText(q.getSampleSolution());
            lblSampleExplanation.setText(q.getSampleExplanation());
        }

        loadReplies();
    }

    @FXML
    private void handleToggleHint() {
        hintVisible = !hintVisible;
        lblHint.setVisible(hintVisible);
        lblHint.setManaged(hintVisible);
        btnToggleHint.setText(hintVisible ? "Hide Hint" : "Show Hint");
    }

    @FXML
    private void handleViewSolution() {
        solutionVisible = !solutionVisible;
        lblSampleSolution.setVisible(solutionVisible);
        lblSampleSolution.setManaged(solutionVisible);
        lblSampleExplanation.setVisible(solutionVisible);
        lblSampleExplanation.setManaged(solutionVisible);
        btnViewSolution.setText(solutionVisible ? "Hide Solution" : "View Solution");
    }

    private void loadReplies() {
        repliesContainer.getChildren().clear();

        if (currentQuestion == null || currentQuestion.getReplies() == null) return;

        for (Reply r : currentQuestion.getReplies()) {
            repliesContainer.getChildren().add(buildReplyNode(r, 0));
        }
    }

    private VBox buildReplyNode(Reply reply, int depth) {
        VBox box = new VBox(3);

        Label author = new Label(
            reply.getAuthor().getUsername() + " • " + reply.getDatePosted()
        );
        author.setStyle("-fx-font-size: 11px; -fx-text-fill: #777;");

        Label content = new Label(reply.getContent());
        content.setWrapText(true);

        box.getChildren().addAll(author, content);

        box.setStyle(
            "-fx-background-color: #f6f6f6;" +
            "-fx-padding: 8;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 6;" +
            "-fx-translate-x: " + (depth * 20) + ";"
        );

        if (reply.getReplies() != null) {
            for (Reply child : reply.getReplies()) {
                box.getChildren().add(buildReplyNode(child, depth + 1));
            }
        }

        return box;
    }

    @FXML
    private void handleAddReply() {
        if (currentQuestion == null) return;

        String text = replyInput.getText();
        if (text == null || text.isBlank()) return;

        facade.addComment(currentQuestion, "", text);

        replyInput.clear();
        loadReplies();
    }

    @FXML
    private void handleBackToQuestions() {
        MainController main = getMainController();
        if (main != null) {
            main.showPage("question.fxml");
        }
    }

    private MainController getMainController() {
        if (lblTitle.getScene() == null) return null;
        Object controller = lblTitle.getScene().getRoot().getUserData();
        return (controller instanceof MainController) ? (MainController) controller : null;
    }
}