package com.model;
 
import java.util.ArrayList;
import java.util.UUID;
 
/**
 * Represents a user's submitted code solution for a question.
 * Can be upvoted, accepted, edited, or deleted.
 * @author Thomas Dunn, James Gessler
 */
public class Solution {
    protected User author;
    protected Question question;
    private UUID solutionId;
    private String content;
    private int upvotes;
    private boolean isAccepted;
    private ArrayList<Reply> comments;
 
    /**
     * Constructs a Solution with the given author, question, and content.
     * @param author the user who submitted the solution
     * @param question the question this solution answers
     * @param content the submitted code
     */
    public Solution(User author, Question question, String content) {
        this.author = author;
        this.question = question;
        this.content = content;
        this.solutionId = UUID.randomUUID();
        this.upvotes = 0;
        this.isAccepted = false;
        this.comments = new ArrayList<>();
    }
 
    /**
     * Returns the unique ID of this solution.
     * @return the solution UUID
     */
    public UUID getSolutionId() { return solutionId; }
 
    /**
     * Returns the author of this solution.
     * @return the solution author
     */
    public User getAuthor() { return author; }
 
    /**
     * Returns the question this solution answers.
     * @return the associated question
     */
    public Question getQuestion() { return question; }
 
    /**
     * Returns the submitted code content.
     * @return the solution content
     */
    public String getContent() { return content; }
 
    /**
     * Returns the number of upvotes this solution has received.
     * @return the upvote count
     */
    public int getUpvotes() { return upvotes; }
 
    /**
     * Returns whether this solution has been accepted.
     * @return true if accepted, false otherwise
     */
    public boolean isAccepted() { return isAccepted; }
 
    /**
     * Returns the list of comments on this solution.
     * @return the list of comments
     */
    public ArrayList<Reply> getComments() { return comments; }
 
    /**
     * Sets the content of this solution.
     * @param content the new content
     */
    public void setContent(String content) { this.content = content; }
 
    /**
     * Sets the upvote count for this solution.
     * @param upvotes the new upvote count
     */
    public void setUpvotes(int upvotes) { this.upvotes = upvotes; }
 
    /**
     * Sets whether this solution is accepted.
     * @param accepted true to mark as accepted, false otherwise
     */
    public void setAccepted(boolean accepted) { this.isAccepted = accepted; }
 
    /**
     * Updates the content of this solution.
     * @param content the new content
     */
    public void edit(String content) { this.content = content; }
 
    /**
     * Increments the upvote count by one.
     */
    public void upvote() { this.upvotes++; }
 
    /**
     * Marks this solution as deleted by replacing the content with a placeholder.
     */
    public void delete() { this.content = "[deleted]"; this.isAccepted = false; }
 
    /**
     * Adds a comment to this solution.
     * @param reply the comment to add
     */
    public void addComment(Reply reply) { if (reply != null) comments.add(reply); }
}