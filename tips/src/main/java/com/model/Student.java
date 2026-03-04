package com.model;

import java.util.ArrayList;

import main.java.com.model.AccountType;
import main.java.com.model.Question;

/**
 * A student user
 * @author Samuel Britton
 */
public class Student extends User {
    private int streak;
    private ArrayList<Question> favQuestions;
    private ArrayList<Comment> Comments;
    private String classification;

    public Student(int userId, String username, String password, String email) {
        super(userId, username, password, email, AccountType.STUDENT);
        this.streak = 0;
        this.favQuestions = new ArrayList<>();
        this.Comments = new ArrayList<>();
        this.classification = "Freshman";
    }

    /**
    * Getters
    */
    public int getStreak() { 
        return streak; 
    }
    public ArrayList<Question> getFavQuestions() { 
        return favQuestions; 
    }
    public ArrayList<Comment> getComments() { 
        return Comments; 
    }
    public String getClassification() { 
        return classification; 
    }

    /*
    * Setters
    */
    public void setStreak(int streak) { 
        this.streak = streak; 
    }
    public void setClassification(String classification) { 
        this.classification = classification; 
    }

    /**
     * Resets or starts the user's streak
     */
    public void startStreak() {
        this.streak = 1;
    }

    /**
     * Increases the user's streak by one
     */
    public void incrementStreak() {
        this.streak++;
    }
    /**
     * Adds a favorite question to the user
     */
    public void addFavQuestion(Question q) {
        if (q != null && !favQuestions.contains(q)) favQuestions.add(q);
    }
    /**
     * removes a favorite question from the user
     */
    public void removeFavQuestion(Question q) {
        favQuestions.remove(q);
    }
}
