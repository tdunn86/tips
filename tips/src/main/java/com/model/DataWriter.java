package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Writes user and question data from the model back to JSON files.
 * Extends DataConstants to access all JSON field name keys.
 * @author Oliver Benjamin
 */
public class DataWriter extends DataConstants {

    /**
     * Saves all users to the users JSON file.
     * Overwrites the existing file with the current state of UserList.
     */
    public static void saveUsers() {
        ArrayList<User> users = UserList.getInstance().getAllUsers();
        JSONArray jsonUsers = new JSONArray();
        for (User user : users) jsonUsers.add(getUserJSON(user));

        try (FileWriter file = new FileWriter(USER_FILE_NAME)) {
            file.write(prettyPrint(jsonUsers, 0));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a user into a JSON object.
     * Includes streak and classification for students.
     * @param user the user to convert
     * @return a JSONObject representing the user
     */
    public static JSONObject getUserJSON(User user) {
        JSONObject userDetails = new JSONObject();
        userDetails.put(USER_ID,       user.getUserId());
        userDetails.put(USER_USERNAME, user.getUsername());
        userDetails.put(USER_PASSWORD, user.getPassword());
        userDetails.put(USER_EMAIL,    user.getEmail());
        userDetails.put(USER_TYPE,     user.getAccountType().toString());
        if (user instanceof Student) {
            Student s = (Student) user;
            userDetails.put(USER_STREAK,         s.getStreak());
            userDetails.put(USER_CLASSIFICATION, s.getClassification());
        }
        return userDetails;
    }

    /**
     * Saves all questions to the questions JSON file.
     * Overwrites the existing file with the current state of QuestionList.
     */
    public static void saveQuestions() {
        ArrayList<Question> questions = QuestionList.getInstance().getAllQuestions();
        JSONArray jsonQuestions = new JSONArray();
        for (Question question : questions) jsonQuestions.add(getQuestionJSON(question));

        try (FileWriter file = new FileWriter(QUESTION_FILE_NAME)) {
            file.write(prettyPrint(jsonQuestions, 0));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a question into a JSON object.
     * Includes all solutions and replies attached to the question.
     * @param question the question to convert
     * @return a JSONObject representing the question
     */
    public static JSONObject getQuestionJSON(Question question) {
        JSONObject q = new JSONObject();
        q.put(QUESTION_ID,                   question.getQuestionID().toString());
        q.put(QUESTION_TITLE,                question.getTitle());
        q.put(QUESTION_PROMPT,               question.getPrompt());
        q.put(QUESTION_DIFFICULTY,           question.getDifficulty().toString());
        q.put(QUESTION_LANGUAGE,             question.getLanguage().toString());
        q.put(QUESTION_COURSE,               question.getCourse().toString());
        q.put(QUESTION_IS_SOLUTION_REVEALED, question.isSolutionRevealed());
        q.put(QUESTION_SAMPLE_SOLUTION,      question.getSampleSolution());
        q.put(QUESTION_SAMPLE_EXPLANATION,   question.getSampleExplanation());
        if (question.getAuthor() != null)
            q.put(QUESTION_AUTHOR_ID, question.getAuthor().getUserId());

        JSONArray jsonSolutions = new JSONArray();
        for (Solution solution : question.getSolutions())
            jsonSolutions.add(getSolutionJSON(solution));
        q.put(QUESTION_SOLUTIONS, jsonSolutions);

        JSONArray jsonReplies = new JSONArray();
        for (Reply reply : question.getReplies())
            jsonReplies.add(getReplyJSON(reply));
        q.put(QUESTION_REPLIES, jsonReplies);

        return q;
    }

    /**
     * Converts a solution into a JSON object.
     * @param solution the solution to convert
     * @return a JSONObject representing the solution
     */
    public static JSONObject getSolutionJSON(Solution solution) {
        JSONObject s = new JSONObject();
        s.put(SOLUTION_ID,       solution.getSolutionId().toString());
        s.put(SOLUTION_CONTENT,  solution.getContent());
        s.put(SOLUTION_UPVOTES,  solution.getUpvotes());
        s.put(SOLUTION_ACCEPTED, solution.isAccepted());
        if (solution.getAuthor() != null)
            s.put(SOLUTION_AUTHOR_ID, solution.getAuthor().getUserId());
        return s;
    }

    /**
     * Converts a reply into a JSON object.
     * Recursively includes any nested replies.
     * @param reply the reply to convert
     * @return a JSONObject representing the reply
     */
    public static JSONObject getReplyJSON(Reply reply) {
        JSONObject r = new JSONObject();
        r.put(REPLY_ID,          reply.getReplyId().toString());
        r.put(REPLY_TITLE,       reply.getTitle());
        r.put(REPLY_CONTENT,     reply.getContent());
        r.put(REPLY_UPVOTES,     reply.getUpvotes());
        r.put(REPLY_ACCEPTED,    reply.isAccepted());
        r.put(REPLY_DATE_POSTED, reply.getDatePosted());
        if (reply.getAuthor() != null)
            r.put(REPLY_AUTHOR_ID, reply.getAuthor().getUserId());

        JSONArray nestedReplies = new JSONArray();
        for (Reply nested : reply.getReplies())
            nestedReplies.add(getReplyJSON(nested));
        r.put(QUESTION_REPLIES, nestedReplies);

        return r;
    }

    // ===================== PRETTY PRINTER =====================

    /**
     * Recursively pretty prints a JSONArray or JSONObject with indentation.
     * Produces readable output without any extra dependencies beyond json-simple.
     * @param value the JSON value to format
     * @param depth the current indentation depth
     * @return a formatted JSON string
     */
    @SuppressWarnings("unchecked")
    private static String prettyPrint(Object value, int depth) {
        String indent  = "  ".repeat(depth);
        String indent1 = "  ".repeat(depth + 1);

        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            if (array.isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < array.size(); i++) {
                sb.append(indent1).append(prettyPrint(array.get(i), depth + 1));
                if (i < array.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(indent).append("]");
            return sb.toString();

        } else if (value instanceof JSONObject) {
            JSONObject obj = (JSONObject) value;
            if (obj.isEmpty()) return "{}";
            ArrayList<String> keys = new ArrayList<>(obj.keySet());
            java.util.Collections.sort(keys);
            StringBuilder sb = new StringBuilder("{\n");
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i);
                Object val = obj.get(key);
                sb.append(indent1)
                  .append("\"").append(key).append("\": ")
                  .append(prettyPrint(val, depth + 1));
                if (i < keys.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(indent).append("}");
            return sb.toString();

        } else if (value instanceof String) {
            String s = (String) value;
            s = s.replace("\\", "\\\\")
                 .replace("\"", "\\\"")
                 .replace("\n", "\\n")
                 .replace("\r", "\\r")
                 .replace("\t", "\\t");
            return "\"" + s + "\"";

        } else if (value == null) {
            return "null";

        } else {
            return value.toString();
        }
    }
}