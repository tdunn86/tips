package com.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A user with admin privileges
 * @author Samuel Britton
 */
public class Admin extends User {
    public List<Question> createdQuestions;
    public int totalQuestionsCreated;

    public Admin(int userId, String username, String password, String email) {
        super(userId, username, password, email, AccountType.ADMIN);
        this.createdQuestions = new ArrayList<>();
        this.totalQuestionsCreated = 0;
    }

    /**
     * Grants editor status to a user
     * @param userid The ID of the user to promote
     * @return True if approved, false if not
     */
    public boolean approveEditor(int userid) {
        User user = UserList.getInstance().getUserById(userid);
        if (user != null) {
            user.setAccountType(AccountType.EDITOR);
            return true;
        }
        return false;
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
