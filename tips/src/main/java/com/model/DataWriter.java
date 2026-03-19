package com.model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author Oliver Benjamin
 * DataWriter class for writing user and question data to JSON files
 */
public class DataWriter extends DataConstants {

    // ===================== USERS =====================

    public static void saveUsers() {
        UserList userList = UserList.getInstance();
        ArrayList<User> users = userList.getAllUsers();
        JSONArray jsonUsers = new JSONArray();
        for (User user : users) jsonUsers.add(getUserJSON(user));

        try (FileWriter file = new FileWriter(USER_FILE_NAME)) {
            file.write(jsonUsers.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getUserJSON(User user) {
        JSONObject userDetails = new JSONObject();
        userDetails.put(USER_ID,       user.getUserId());
        userDetails.put(USER_USERNAME, user.getUsername());
        userDetails.put(USER_PASSWORD, user.getPassword());
        userDetails.put(USER_EMAIL,    user.getEmail());
        userDetails.put(USER_TYPE,     user.getAccountType().toString()); // FIXED: was USER_ACCOUNT_TYPE

        if (user instanceof Student) {
            Student s = (Student) user;
            userDetails.put(USER_STREAK,         s.getStreak());
            userDetails.put(USER_CLASSIFICATION, s.getClassification());
        }
        return userDetails;
    }

    // ===================== QUESTIONS =====================

    public static void saveQuestions() {
        QuestionList questionList = QuestionList.getInstance();
        ArrayList<Question> questions = questionList.getAllQuestions();
        JSONArray jsonQuestions = new JSONArray();
        for (Question question : questions) jsonQuestions.add(getQuestionJSON(question));

        try (FileWriter file = new FileWriter(QUESTION_FILE_NAME)) {
            file.write(jsonQuestions.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getQuestionJSON(Question question) {
        JSONObject q = new JSONObject();
        q.put(QUESTION_ID,                  question.getQuestionID().toString());
        q.put(QUESTION_TITLE,               question.getTitle());
        q.put(QUESTION_PROMPT,              question.getPrompt());
        q.put(QUESTION_DIFFICULTY,          question.getDifficulty().toString());
        q.put(QUESTION_LANGUAGE,            question.getLanguage().toString());
        q.put(QUESTION_COURSE,              question.getCourse().toString());
        q.put(QUESTION_IS_SOLUTION_REVEALED, question.isSolutionRevealed());
        q.put(QUESTION_SAMPLE_SOLUTION,     question.getSampleSolution());
        q.put(QUESTION_SAMPLE_EXPLANATION,  question.getSampleExplanation());

        if (question.getAuthor() != null)
            q.put(QUESTION_AUTHOR_ID, question.getAuthor().getUserId());

        // Solutions (answers)
        JSONArray jsonSolutions = new JSONArray();
        for (Solution solution : question.getSolutions())
            jsonSolutions.add(getSolutionJSON(solution));
        q.put(QUESTION_SOLUTIONS, jsonSolutions);

        // Replies (comments, recursive)
        JSONArray jsonReplies = new JSONArray();
        for (Reply reply : question.getReplies())
            jsonReplies.add(getReplyJSON(reply));
        q.put(QUESTION_REPLIES, jsonReplies);

        return q;
    }

    // ===================== SOLUTIONS =====================

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

    // ===================== REPLIES (recursive) =====================

    public static JSONObject getReplyJSON(Reply reply) {
        JSONObject r = new JSONObject();
        r.put(REPLY_ID,       reply.getReplyId().toString());
        r.put(REPLY_TITLE,    reply.getTitle());
        r.put(REPLY_CONTENT,  reply.getContent());
        r.put(REPLY_UPVOTES,  reply.getUpvotes());
        r.put(REPLY_ACCEPTED, reply.isAccepted());
        if (reply.getAuthor() != null)
            r.put(REPLY_AUTHOR_ID, reply.getAuthor().getUserId());

        // Recursive: save nested replies (comments on comments)
        JSONArray nestedReplies = new JSONArray();
        for (Reply nested : reply.getReplies())
            nestedReplies.add(getReplyJSON(nested));
        r.put(QUESTION_REPLIES, nestedReplies);

        return r;
    }
}