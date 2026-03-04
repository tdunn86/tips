package com.model;

<<<<<<< HEAD
/**
 * @author: Oliver Benjamin
 * Constants for data writing and reading
 * 
 * 
**/
public abstract class DataConstants {

    // ===================== FILE NAMES =====================
    protected static final String USER_FILE_NAME     = "data/users.json";
    protected static final String QUESTION_FILE_NAME = "data/questions.json";

    // ===================== USER FIELDS =====================
    protected static final String USER_ID           = "userId";
    protected static final String USER_USERNAME     = "username";
    protected static final String USER_PASSWORD     = "password";
    protected static final String USER_EMAIL        = "email";
    protected static final String USER_ACCOUNT_TYPE = "accountType";

    // ===================== QUESTION FIELDS =====================
    protected static final String QUESTION_ID                  = "questionID";
    protected static final String QUESTION_TITLE               = "title";
    protected static final String QUESTION_PROMPT              = "prompt";
    protected static final String QUESTION_DIFFICULTY          = "difficulty";
    protected static final String QUESTION_LANGUAGE            = "language";
    protected static final String QUESTION_AUTHOR              = "author";
    protected static final String QUESTION_IS_SOLUTION_REVEALED = "isSolutionRevealed";
    protected static final String QUESTION_SAMPLE_SOLUTION     = "sampleSolution";
    protected static final String QUESTION_SAMPLE_EXPLANATION  = "sampleExplanation";
    protected static final String QUESTION_COURSES             = "courses";
    protected static final String QUESTION_IMAGES              = "images";
    protected static final String QUESTION_ATTACHMENTS         = "attachments";
    protected static final String QUESTION_SOLUTIONS           = "solutions";
    protected static final String QUESTION_REPLIES             = "replies";

    // ===================== SOLUTION FIELDS =====================
    protected static final String SOLUTION_ID      = "solutionId";
    protected static final String SOLUTION_CONTENT = "content";
    protected static final String SOLUTION_AUTHOR  = "author";

    // ===================== REPLY FIELDS =====================
    protected static final String REPLY_ID      = "replyId";
    protected static final String REPLY_CONTENT = "content";
    protected static final String REPLY_AUTHOR  = "author";
}
=======
public class DataConstants {
    // File paths
    public static final String USER_FILE_NAME     = "json/users.json";
    public static final String QUESTION_FILE_NAME = "json/questions.json";

    // User JSON keys
    public static final String USER_ID             = "userId";
    public static final String USER_USERNAME       = "username";
    public static final String USER_PASSWORD       = "password";
    public static final String USER_EMAIL          = "email";
    public static final String USER_TYPE           = "userType";
    public static final String USER_CLASSIFICATION = "classification";
    public static final String USER_STREAK         = "streak";

    // Question JSON keys
    public static final String QUESTION_ID         = "questionID";
    public static final String QUESTION_TITLE      = "title";
    public static final String QUESTION_PROMPT     = "prompt";
    public static final String QUESTION_DIFFICULTY = "difficulty";
    public static final String QUESTION_LANGUAGE   = "language";
    public static final String QUESTION_COURSE     = "course";
    public static final String QUESTION_AUTHOR_ID  = "authorId";
    public static final String QUESTION_SAMPLE_SOL = "sampleSolution";
    public static final String QUESTION_SAMPLE_EXP = "sampleExplanation";
    public static final String QUESTION_REVEALED   = "isSolutionRevealed";

    // Solution JSON keys
    public static final String SOLUTION_AUTHOR_ID  = "authorId";
    public static final String SOLUTION_CONTENT    = "content";
    public static final String SOLUTION_UPVOTES    = "upvotes";
    public static final String SOLUTION_ACCEPTED   = "isAccepted";

    // Reply JSON keys
    public static final String REPLY_AUTHOR_ID     = "authorId";
    public static final String REPLY_TITLE         = "title";
    public static final String REPLY_CONTENT       = "content";
    public static final String REPLY_UPVOTES       = "upvotes";
    public static final String REPLY_ACCEPTED      = "isAccepted";
}
>>>>>>> af76fbbacba1cc5d1c654dee7568ef0d10064b10
