package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A user with editor permissions
 */
public class Editor extends User {
    public List<Question> createdQuestions;
    public int totalQuestionsCreated;

    public Editor(int userId, String username, String password, String email) {
        super(userId, username, password, email, AccountType.EDITOR);
        this.createdQuestions = new ArrayList<>();
        this.totalQuestionsCreated = 0;
    }

    /**
     * Adds a new question
     * @param q The Question to be created
     * @return The created question
     */
    public Question createQuestion(Question q) {
        if (q != null) { createdQuestions.add(q); totalQuestionsCreated++; }
        return q;
    }

    /**
     * Edites a question
     * @param q The Question to be edited
     * @return The modified question
     */
    public Question editQuestion(Question q) {
        return q;
    }

    /**
     * Removes a question
     * @param q The Question to remove
     * @return The removed question
     */
    public Question deleteQuestion(Question q) {
        createdQuestions.remove(q);
        return q;
    }

    public void createSection() {

    }
}
