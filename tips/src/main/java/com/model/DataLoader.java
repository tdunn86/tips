package com.model;

import java.io.FileReader;
import java.util.ArrayList;

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

                int userId = ((Long) userObject.get("userId")).intValue();
                String username = (String) userObject.get("username");
                String password = (String) userObject.get("password");
                String email = (String) userObject.get("email");
                String userType = (String) userObject.get("userType");

                User user = null;

                switch (userType) {
                    case "Student":
                        user = new Student(userId, username, password, email);
                        break;

                    case "Editor":
                        user = new Editor(userId, username, password, email);
                        break;

                    case "Admin":
                        user = new Admin(userId, username, password, email);
                        break;
                }

                users.add(user);
            }

        } catch (Exception e) {
        }

        return users;
    }

    public static ArrayList<Question> getQuestions() {

        ArrayList<Question> questions = new ArrayList<>();

        try {
            FileReader reader = new FileReader(QUESTION_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray questionJSON = (JSONArray) parser.parse(reader);

            for (Object obj : questionJSON) {

                JSONObject questionObject = (JSONObject) obj;

                String title = (String) questionObject.get("title");
                String prompt = (String) questionObject.get("prompt");
                String difficultyStr = (String) questionObject.get("difficulty");
                String languageStr = (String) questionObject.get("language");
                String courseStr = (String) questionObject.get("course");

                Difficulty difficulty = Difficulty.valueOf(difficultyStr);
                Language language = Language.valueOf(languageStr);
                Course course = Course.valueOf(courseStr);

                Question question = new Question(
                        title,
                        prompt,
                        difficulty,
                        language,
                        course
                );

                questions.add(question);
            }

        } catch (Exception e) {
        }

        return questions;
    }
}