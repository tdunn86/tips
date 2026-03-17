package com.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataLoader extends DataConstants {
 
    public static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
 
        try {
            FileReader reader = new FileReader(USER_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray userJSON = (JSONArray) parser.parse(reader);
 
            for (Object obj : userJSON) {
                JSONObject userObject = (JSONObject) obj;
 
                int    userId   = ((Long)   userObject.get(USER_ID)).intValue();
                String username = (String)  userObject.get(USER_USERNAME);
                String password = (String)  userObject.get(USER_PASSWORD);
                String email    = (String)  userObject.get(USER_EMAIL);
                String userType = (String)  userObject.get(USER_TYPE);
 
                User user = null;
                switch (userType) {
                    case "Student":
                        Student student = new Student(userId, username, password, email);
                        if (userObject.containsKey(USER_CLASSIFICATION))
                            student.setClassification((String) userObject.get(USER_CLASSIFICATION));
                        if (userObject.containsKey(USER_STREAK))
                            student.setStreak(((Long) userObject.get(USER_STREAK)).intValue());
                        user = student;
                        break;
                    case "Editor":
                        user = new Editor(userId, username, password, email);
                        break;
                    case "Admin":
                        user = new Admin(userId, username, password, email);
                        break;
                }
 
                if (user != null) users.add(user);
            }
 
        } catch (Exception e) {
            System.out.println("EXCEPTION loading users: " + e.getMessage());
            e.printStackTrace();
        }
 
        return users;
    }
 
    public static ArrayList<Question> getQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        ArrayList<User> users = getUsers();
 
        try {
            FileReader reader = new FileReader(QUESTION_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray questionJSON = (JSONArray) parser.parse(reader);
 
            for (Object obj : questionJSON) {
                JSONObject questionObject = (JSONObject) obj;
 
                String title         = (String) questionObject.get(QUESTION_TITLE);
                String prompt        = (String) questionObject.get(QUESTION_PROMPT);
                String difficultyStr = (String) questionObject.get(QUESTION_DIFFICULTY);
                String languageStr   = (String) questionObject.get(QUESTION_LANGUAGE);
 
                // FIX: use QUESTION_COURSE (primary course) for the constructor
                String courseStr = (String) questionObject.get(QUESTION_COURSE);
 
                Difficulty difficulty = Difficulty.valueOf(difficultyStr);
                Language   language   = Language.valueOf(languageStr);
                Course     course     = Course.valueOf(courseStr);
 
                Question question = new Question(title, prompt, difficulty, language, course);
 
                // FIX: restore stable UUID
                if (questionObject.containsKey(QUESTION_ID)) {
                    question.setQuestionID(UUID.fromString(
                        (String) questionObject.get(QUESTION_ID)));
                }
 
                // FIX: restore full courses array
                if (questionObject.containsKey(QUESTION_COURSES)) {
                    JSONArray coursesArray = (JSONArray) questionObject.get(QUESTION_COURSES);
                    for (Object c : coursesArray) {
                        question.getCourses().add(Course.valueOf((String) c));
                    }
                }
 
                if (questionObject.containsKey("hint"))
                    question.setHint((String) questionObject.get("hint"));
 
                // FIX: use full-name constants (aliases removed from DataConstants)
                if (questionObject.containsKey(QUESTION_SAMPLE_SOLUTION))
                    question.setSampleSolution((String) questionObject.get(QUESTION_SAMPLE_SOLUTION));
 
                if (questionObject.containsKey(QUESTION_SAMPLE_EXPLANATION))
                    question.setSampleExplanation((String) questionObject.get(QUESTION_SAMPLE_EXPLANATION));
 
                if (Boolean.TRUE.equals(questionObject.get(QUESTION_IS_SOLUTION_REVEALED)))
                    question.revealSolution();
 
                // FIX: use QUESTION_AUTHOR_ID — matches what DataWriter saves
                if (questionObject.containsKey(QUESTION_AUTHOR_ID)) {
                    int authorId = ((Long) questionObject.get(QUESTION_AUTHOR_ID)).intValue();
                    findUserById(users, authorId).ifPresent(question::setAuthor);
                }
 
                // Solutions
                if (questionObject.containsKey(QUESTION_SOLUTIONS)) {
                    JSONArray solutionsJSON = (JSONArray) questionObject.get(QUESTION_SOLUTIONS);
                    for (Object sObj : solutionsJSON) {
                        JSONObject solutionObject = (JSONObject) sObj;
 
                        // FIX: use SOLUTION_AUTHOR_ID — matches what DataWriter saves
                        if (!solutionObject.containsKey(SOLUTION_AUTHOR_ID)) continue;
                        int    authorId = ((Long) solutionObject.get(SOLUTION_AUTHOR_ID)).intValue();
                        String content  = (String) solutionObject.get(SOLUTION_CONTENT);
 
                        findUserById(users, authorId).ifPresent(author -> {
                            Solution solution = new Solution(author, question, content);
                            if (solutionObject.containsKey(SOLUTION_UPVOTES))
                                solution.setUpvotes(((Long) solutionObject.get(SOLUTION_UPVOTES)).intValue());
                            if (Boolean.TRUE.equals(solutionObject.get(SOLUTION_ACCEPTED)))
                                solution.setAccepted(true);
                            question.addSolution(solution);
                        });
                    }
                }
 
                // Replies
                if (questionObject.containsKey(QUESTION_REPLIES)) {
                    JSONArray repliesJSON = (JSONArray) questionObject.get(QUESTION_REPLIES);
                    for (Object rObj : repliesJSON) {
                        JSONObject replyObject = (JSONObject) rObj;
 
                        // FIX: use REPLY_AUTHOR_ID — matches what DataWriter saves
                        if (!replyObject.containsKey(REPLY_AUTHOR_ID)) continue;
                        int    authorId = ((Long) replyObject.get(REPLY_AUTHOR_ID)).intValue();
                        String content  = (String) replyObject.get(REPLY_CONTENT);
 
                        findUserById(users, authorId).ifPresent(author -> {
                            Reply reply = new Reply(author, question, content);
                            if (replyObject.containsKey(REPLY_TITLE))
                                reply.setTitle((String) replyObject.get(REPLY_TITLE));
                            if (replyObject.containsKey(REPLY_UPVOTES))
                                reply.setUpvotes(((Long) replyObject.get(REPLY_UPVOTES)).intValue());
                            if (Boolean.TRUE.equals(replyObject.get(REPLY_ACCEPTED)))
                                reply.setAccepted(true);
                            question.addReply(reply);
                        });
                    }
                }
 
                questions.add(question);
            }
 
        } catch (Exception e) {
            System.out.println("EXCEPTION loading questions: " + e.getMessage());
            e.printStackTrace();
        }
 
        return questions;
    }
 
    private static java.util.Optional<User> findUserById(ArrayList<User> users, int userId) {
        return users.stream().filter(u -> u.getUserId() == userId).findFirst();
    }
}
 