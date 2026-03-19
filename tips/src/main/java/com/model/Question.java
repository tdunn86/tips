package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a technical interview question
 * @author Samuel Britton
 */
public class Question {
    private String title;
    private String prompt;
    private String hint;
    private UUID questionID;
    private Difficulty difficulty;
    private Language language;
    private Course course;
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
     * Constructor for a question entry
     * @param title The title of the question
     * @param prompt The content of the question
     * @param difficulty The difficulty of the question
     * @param language The programming language of the question
     * @param course The course the question is from
     */
    public Question(String title, String prompt,
        Difficulty difficulty, Language language, Course course) {
        this.title = title;
        this.prompt = prompt;
        this.difficulty = difficulty;
        this.language = language;
        this.course = course;
        this.questionID = UUID.randomUUID();
        this.isSolutionRevealed = false;
        this.solutions = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.images = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.replies = new ArrayList<>();
    }

    // Getters
    public String getTitle() { return title; }
    public String getPrompt() { return prompt; }
    public String getHint() { return hint; }
    public UUID getQuestionID() { return questionID; }
    public Difficulty getDifficulty() { return difficulty; }
    public Language getLanguage() { return language; }
    public Course getCourse() { return course; }
    public ArrayList<Course> getCourses() { return courses; }
    public User getAuthor() { return author; }
    public boolean isSolutionRevealed() { return isSolutionRevealed; }
    public ArrayList<Solution> getSolutions() { return solutions; }
    public String getSampleSolution() { return sampleSolution; }
    public String getSampleExplanation() { return sampleExplanation; }
    public List<String> getImages() { return images; }
    public List<String> getAttachments() { return attachments; }
    public List<Reply> getReplies() { return replies; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public void setHint(String hint) { this.hint = hint; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public void setLanguage(Language language) { this.language = language; }
    public void setCourse(Course course) { this.course = course; }
    public void setAuthor(User author) { this.author = author; }
    public void setSampleSolution(String sampleSolution) { this.sampleSolution = sampleSolution; }
    public void setSampleExplanation(String sampleExplanation) { this.sampleExplanation = sampleExplanation; }

    /**
     * Displays the solution
     */
    public void showSolution() {
        this.isSolutionRevealed = true;
    }

    public void revealSolution() {
        this.isSolutionRevealed = true;
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
        this.isSolutionRevealed = false;
        this.solutions.clear();
    }

    /**
     * Checks if the question has been completed
     * @return True if solution is revealed, false otherwise
     */
    public boolean isComplete() {
        return isSolutionRevealed;
    }

    public void addSolution(Solution solution) {
        if (solution != null) solutions.add(solution);
    }

    public void addReply(Reply reply) {
        if (reply != null) replies.add(reply);
    }

    public String toString() {
        return "Question{id=" + questionID + ", title='" + title +
               "', difficulty=" + difficulty + ", course=" + course + "}";
    }

    public void setQuestionID(UUID questionID) {
        this.questionID = questionID;
    }
}