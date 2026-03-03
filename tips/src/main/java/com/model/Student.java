package com.model;

import java.util.ArrayList;

/**
 * A specialized user type that tracks learning progress
 * @author Thomas Dunn, James Gessler
 */
public class Student implements User {
    private int streak;
    private ArrayList<Question> favQuestions;
    private ArrayList<Comment> Comments;
    private String classification;

    /**
     * Resets or starts the user's faily activity streak
     */
    public void startStreak() {

    }

    /**
     * Increases the user's streak count by one
     */
    public void incrementStreak() {

    }


}