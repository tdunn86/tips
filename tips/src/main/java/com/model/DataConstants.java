package com.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Holds all constants used for reading and writing JSON data.
 * Contains file paths and field name keys for users, questions, solutions, and replies.
 * @author Oliver Benjamin
 */
public class DataConstants {

    // ===================== FILE PATHS =====================
    private static final Path JSON_DIR = resolveJsonDir();

    public static final String USER_FILE_NAME = JSON_DIR.resolve("users.json").toString();
    public static final String QUESTION_FILE_NAME = JSON_DIR.resolve("questions.json").toString();

    private static Path resolveJsonDir() {
        Path cwd = Paths.get("").toAbsolutePath().normalize();

        Path[] candidates = new Path[] {
            cwd.resolve("json"),
            cwd.resolve("..").resolve("json").normalize(),
            cwd.resolve("..").resolve("..").resolve("json").normalize()
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate;
            }
        }

        // Last resort: current working directory/json
        return cwd.resolve("json");
    }

    // ===================== USER FIELDS =====================
    public static final String USER_ID             = "userId";
    public static final String USER_USERNAME       = "username";
    public static final String USER_PASSWORD       = "password";
    public static final String USER_EMAIL          = "email";
    public static final String USER_TYPE           = "userType";
    public static final String USER_CLASSIFICATION = "classification";
    public static final String USER_STREAK         = "streak";

    // ===================== QUESTION FIELDS =====================
    public static final String QUESTION_ID                   = "questionID";
    public static final String QUESTION_TITLE                = "title";
    public static final String QUESTION_PROMPT               = "prompt";
    public static final String QUESTION_DIFFICULTY           = "difficulty";
    public static final String QUESTION_LANGUAGE             = "language";
    public static final String QUESTION_COURSE               = "course";
    public static final String QUESTION_AUTHOR_ID            = "authorId";
    public static final String QUESTION_IS_SOLUTION_REVEALED = "isSolutionRevealed";
    public static final String QUESTION_SAMPLE_SOLUTION      = "sampleSolution";
    public static final String QUESTION_SAMPLE_EXPLANATION   = "sampleExplanation";
    public static final String QUESTION_IMAGES               = "images";
    public static final String QUESTION_ATTACHMENTS          = "attachments";
    public static final String QUESTION_SOLUTIONS            = "solutions";
    public static final String QUESTION_REPLIES              = "replies";

    // ===================== SOLUTION FIELDS =====================
    public static final String SOLUTION_ID        = "solutionId";
    public static final String SOLUTION_CONTENT   = "content";
    public static final String SOLUTION_AUTHOR_ID = "authorId";
    public static final String SOLUTION_UPVOTES   = "upvotes";
    public static final String SOLUTION_ACCEPTED  = "isAccepted";

    // ===================== REPLY FIELDS =====================
    public static final String REPLY_ID          = "replyId";
    public static final String REPLY_CONTENT     = "content";
    public static final String REPLY_AUTHOR_ID   = "authorId";
    public static final String REPLY_TITLE       = "title";
    public static final String REPLY_UPVOTES     = "upvotes";
    public static final String REPLY_ACCEPTED    = "isAccepted";
    public static final String REPLY_DATE_POSTED = "datePosted";
}