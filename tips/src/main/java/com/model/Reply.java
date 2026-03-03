package com.model;

/**
 * Represents a reply to a question in the TIPS system
 * @author Thomas Dunn, James Gessler
 */
public class Reply {
    /** The user who authored this reply. */
    protected User author;
    /** The question this reply is associated with. */
    protected Question question;
    /** The title of the reply. */
    private String title;
    /** The content of the reply. */
    private String content;
    /** The number of upvotes this reply has received. */
    private int upvotes;
    /** Whether this reply has been accepted. */
    private boolean isAccepted;

    /**
     * Constructs a new Reply.
     * @param author the user who wrote the reply
     * @param question the question being replied to
     * @param content the content of the reply
     */
    public Reply(User author, Question question, String content) {
        // TODO
    }

    /**
     * Adds a comment to this reply.
     * @param title the title of the comment
     * @param content the content of the comment
     * @return the newly created comment
     */
    public Comment addComment(String title, String content) {
        // TODO
        return null;
    }

    /**
     * Reports this reply for a given reason.
     * @param reason the reason for reporting
     */
    public void report(String reason) {
        // TODO
    }
}