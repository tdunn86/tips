package com.model;

/**
 * Represents the user feedback on a solution
 * @author Thomas Dunn, James Gessler
 */
public class Reply {
    protected User author;
    protected Question question;
    private Title title;
    private String content;
    private int upvotes;
    private boolean isAccepted;

    /**
     * Constructor to initialize a reply
     * @param author The author of a reply
     * @param question The question being replied to
     * @param content The content of the reply
     */
    public Reply(User author, Question question, String content) {

    }

    /**
     * Flags a reply for administrative review
     * @param reason A string description explaining the reason for reporting
     */
    public void report(String reason) {

    }
}