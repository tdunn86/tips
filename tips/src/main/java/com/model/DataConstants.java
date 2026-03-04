package com.model;

public class DataConstants {
    // File paths
    public static final String USER_FILE_NAME     = "data/users.json";
    public static final String QUESTION_FILE_NAME = "data/questions.json";

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
