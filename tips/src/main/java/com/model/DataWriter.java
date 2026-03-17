package com.model;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * @author: Oliver Benjamin
 * DataWriter class for writing user and question data to JSON files
 * 
**/

public class DataWriter extends DataConstants {
 
    // ===================== USERS =====================
 
    public static void saveUsers() {
        UserList userList = UserList.getInstance();
        ArrayList<User> users = (ArrayList<User>) userList.getAllUsers();
        JSONArray jsonUsers = new JSONArray();
 
        for (User user : users) {
            jsonUsers.add(getUserJSON(user));
        }
 
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
        userDetails.put(USER_TYPE,     user.getAccountType().toDisplayString());
 
        if (user instanceof Student) {
            Student s = (Student) user;
            userDetails.put(USER_CLASSIFICATION, s.getClassification());
            userDetails.put(USER_STREAK,         s.getStreak());
        }
        return userDetails;
    }
 
    // ===================== QUESTIONS =====================
 
    public static void saveQuestions() {
        QuestionList questionList = QuestionList.getInstance();
        ArrayList<Question> questions = (ArrayList<Question>) questionList.getAllQuestions();
        JSONArray jsonQuestions = new JSONArray();
 
        for (Question question : questions) {
            jsonQuestions.add(getQuestionJSON(question));
        }
 
        try (FileWriter file = new FileWriter(QUESTION_FILE_NAME)) {
            file.write(jsonQuestions.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public static JSONObject getQuestionJSON(Question question) {
        JSONObject questionDetails = new JSONObject();
 
        questionDetails.put(QUESTION_ID,                  question.getQuestionID().toString());
        questionDetails.put(QUESTION_TITLE,               question.getTitle());
        questionDetails.put(QUESTION_PROMPT,              question.getPrompt());
        questionDetails.put(QUESTION_DIFFICULTY,          question.getDifficulty().toString());
        questionDetails.put(QUESTION_LANGUAGE,            question.getLanguage().toString());
        questionDetails.put(QUESTION_IS_SOLUTION_REVEALED, question.isSolutionRevealed());
        questionDetails.put(QUESTION_SAMPLE_SOLUTION,     question.getSampleSolution());
        questionDetails.put(QUESTION_SAMPLE_EXPLANATION,  question.getSampleExplanation());
 
        if (question.getCourse() != null) {
            questionDetails.put(QUESTION_COURSE, question.getCourse().toString());
        }
 
        if (question.getAuthor() != null) {
            questionDetails.put(QUESTION_AUTHOR_ID, question.getAuthor().getUserId());
        }
 
        // Courses array
        JSONArray jsonCourses = new JSONArray();
        for (Course course : question.getCourses()) {
            jsonCourses.add(course.toString());
        }
        questionDetails.put(QUESTION_COURSES, jsonCourses);
 
        // Images
        JSONArray jsonImages = new JSONArray();
        for (String image : question.getImages()) {
            jsonImages.add(image);
        }
        questionDetails.put(QUESTION_IMAGES, jsonImages);
 
        // Attachments
        JSONArray jsonAttachments = new JSONArray();
        for (String attachment : question.getAttachments()) {
            jsonAttachments.add(attachment);
        }
        questionDetails.put(QUESTION_ATTACHMENTS, jsonAttachments);
 
        // Solutions
        JSONArray jsonSolutions = new JSONArray();
        for (Solution solution : question.getSolutions()) {
            jsonSolutions.add(getSolutionJSON(solution));
        }
        questionDetails.put(QUESTION_SOLUTIONS, jsonSolutions);
 
        // Replies
        JSONArray jsonReplies = new JSONArray();
        for (Reply reply : question.getReplies()) {
            jsonReplies.add(getReplyJSON(reply));
        }
        questionDetails.put(QUESTION_REPLIES, jsonReplies);
 
        return questionDetails;
    }
 
    // ===================== SOLUTIONS =====================
 
    public static JSONObject getSolutionJSON(Solution solution) {
        JSONObject solutionDetails = new JSONObject();
        solutionDetails.put(SOLUTION_ID,      solution.getSolutionId().toString());
        solutionDetails.put(SOLUTION_CONTENT, solution.getContent());
        if (solution.getAuthor() != null) {
            solutionDetails.put(SOLUTION_AUTHOR_ID, solution.getAuthor().getUserId());
        }
        return solutionDetails;
    }
 
    // ===================== REPLIES =====================
 
    public static JSONObject getReplyJSON(Reply reply) {
        JSONObject replyDetails = new JSONObject();
        replyDetails.put(REPLY_ID,      reply.getReplyId().toString());
        replyDetails.put(REPLY_CONTENT, reply.getContent());
        if (reply.getTitle() != null) {
            replyDetails.put(REPLY_TITLE, reply.getTitle());
        }
        if (reply.getAuthor() != null) {
            replyDetails.put(REPLY_AUTHOR_ID, reply.getAuthor().getUserId());
        }
        return replyDetails;
    }
}