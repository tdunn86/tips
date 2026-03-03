package com.model;

import java.util.ArrayList;

/**
 * Represents an answer or explanation for a question
 * @author Thomas Dunn, James Gessler
 */
public class Solution {
    protected User author;
    protected Question question;
    private String content;
    private Solution solution;
    private int upvotes;
    private boolean isAccepted;
    private ArrayList<Reply> comments;

    /**
     * The solution to the current question
     * @param author The author of the question
     * @param questionID The ID of the solution
     * @param content The content of the solution
     */
    public Solution(User author, int questionID, String content) {

    }

    /**
     * Edit an existing solution
     * @param content The content of the solution to edit
     */
    public void edit(String content) {

    }

    /**
     * Allows a user to upvote a solution
     */
    public void upvote() {

    }

    /**
     * Delete a solution
     */
    public void delete() {

    }
}