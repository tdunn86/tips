package com.model;

import java.util.UUID;

/**
 * Represents a coding challenge or academic problem
 * @author Thomas Dunn, James Gessler
 */
public class Question {
    private String Title;
    private String Prompt;
    private UUID questionID;
    private Difficulty difficulty;
    private Language language;
    private ArrayList<Course> courses;
    private User author;
    private boolean isSolutionRevealed;
    private ArrayList<Solution> solutions;
    private String sampleSolution;
    private String sampleExplanation;
    private List<String> images;
    private List<String> attachments;
    private List<Reply> replies;

    /**
     * Constructor for a full question entry
     * @param quesitonID The ID of the question
     * @param title The title of the question
     * @param prompt The content of the question
     * @param difficulty The difficulty of the question
     * @param language The programming language of the question
     * @param course The course the question is from
     */
    public Question(int quesitonID, String title, String prompt, 
        Difficulty difficulty, Language language, Course course) {

    }

    /**
     * Displays the solution for the given ID
     */
    public void showSolution(questionID) {

    }

    /**
     * Retrieves a hint to help solve the question
     * @return The hint text
     */
    public String getHint() {
        return "Think harder!";
    }

    /**
     * Submits the user's answer for evaluation
     */
    public void submit() {

    }

    /**
     * Clears current input on the question
     */
    public void reset() {

    }

    /**
     * Checks if the quesiton has been finished
     * @return True if finished, false otherwise
     */
    public boolean isComplete() {
        return false;
    }

    /**
     * Unhides the hidden solution for the user
     */
    public void revealSolution() {

    }

}
