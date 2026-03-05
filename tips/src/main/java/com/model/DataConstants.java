package com.model;

/**
 * @author: Oliver Benjamin
 * Constants for data writing and reading
 * 
 * Merged data constants NEEDS TO BE FIXED - currently contains all constants from both files, including duplicates and variants.
**/

public class DataConstants {
    // ===================== FILE PATHS =====================
    public static final String USER_FILE_NAME     = "json/users.json";
    public static final String QUESTION_FILE_NAME = "json/questions.json";

    // ===================== USER FIELDS =====================
    public static final String USER_ID           = "userId";
    public static final String USER_USERNAME     = "username";
    public static final String USER_PASSWORD     = "password";
    public static final String USER_EMAIL        = "email";

    // account / type related fields (both forms retained)
    public static final String USER_ACCOUNT_TYPE = "accountType"; // original name from first file
    public static final String USER_TYPE         = "userType";    // alternative from second file

    // additional user fields present only in second file
    public static final String USER_CLASSIFICATION = "classification";
    public static final String USER_STREAK         = "streak";

    // ===================== QUESTION FIELDS =====================
    public static final String QUESTION_ID                  = "questionID";
    public static final String QUESTION_TITLE               = "title";
    public static final String QUESTION_PROMPT              = "prompt";
    public static final String QUESTION_DIFFICULTY          = "difficulty";
    public static final String QUESTION_LANGUAGE            = "language";

    public static final String QUESTION_COURSE              = "course";   // singular (second file)
    public static final String QUESTION_COURSES              = "courses";
    // author fields (variants)
    public static final String QUESTION_AUTHOR              = "author";    // original (this could be the username of the author)
    public static final String QUESTION_AUTHOR_ID           = "authorId";  // alternative

    public static final String QUESTION_IS_SOLUTION_REVEALED = "isSolutionRevealed";
    public static final String QUESTION_SAMPLE_SOLUTION     = "sampleSolution";
    public static final String QUESTION_SAMPLE_EXPLANATION  = "sampleExplanation";

    // attachments / images / solutions / replies
    public static final String QUESTION_IMAGES              = "images";
    public static final String QUESTION_ATTACHMENTS         = "attachments";
    public static final String QUESTION_SOLUTIONS           = "solutions";
    public static final String QUESTION_REPLIES             = "replies";

    // shorter variant names included (from second file)
    public static final String QUESTION_SAMPLE_SOL = "sampleSolution";      // alias (same value)
    public static final String QUESTION_SAMPLE_EXP = "sampleExplanation";   // alias (same value)
    public static final String QUESTION_REVEALED   = "isSolutionRevealed";  // alias (same value)

    // ===================== SOLUTION FIELDS =====================
    public static final String SOLUTION_ID      = "solutionId";
    public static final String SOLUTION_CONTENT = "content";

    // author / authorId variants (keep both)
    public static final String SOLUTION_AUTHOR  = "author";
    public static final String SOLUTION_AUTHOR_ID = "authorId";

    // additional solution metadata (from second file)
    public static final String SOLUTION_UPVOTES  = "upvotes";
    public static final String SOLUTION_ACCEPTED = "isAccepted";

    // ===================== REPLY FIELDS =====================
    public static final String REPLY_ID      = "replyId";
    public static final String REPLY_CONTENT = "content";

    // author / authorId / title variants for replies
    public static final String REPLY_AUTHOR  = "author";
    public static final String REPLY_AUTHOR_ID = "authorId";
    public static final String REPLY_TITLE   = "title";

    // additional reply metadata
    public static final String REPLY_UPVOTES  = "upvotes";
    public static final String REPLY_ACCEPTED = "isAccepted";
}
