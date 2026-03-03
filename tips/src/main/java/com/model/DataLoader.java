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
            String path = System.getProperty("user.dir") + "/" + USER_FILE_NAME;
            FileReader reader = new FileReader(path);

            JSONParser parser = new JSONParser();
            JSONArray usersJSON = (JSONArray) parser.parse(reader);

            for (int i = 0; i < usersJSON.size(); i++) {

                JSONObject userJSON = (JSONObject) usersJSON.get(i);

                int userId = ((Long) userJSON.get("userId")).intValue();
                String username = (String) userJSON.get("username");
                String password = (String) userJSON.get("password");
                String email = (String) userJSON.get("email");
                String accountType = (String) userJSON.get("accountType");
                int streak = ((Long) userJSON.get("streak")).intValue();

                JSONArray favArray = (JSONArray) userJSON.get("favQuestions");
                ArrayList<String> favQuestions = new ArrayList<>();
                for (int j = 0; j < favArray.size(); j++) {
                    favQuestions.add((String) favArray.get(j));
                }

                JSONArray commentArray = (JSONArray) userJSON.get("comments");
                ArrayList<Integer> comments = new ArrayList<>();
                for (int j = 0; j < commentArray.size(); j++) {
                    comments.add(((Long) commentArray.get(j)).intValue());
                }

                users.add(new User(userId, username, password, email,
                    accountType, streak, favQuestions, comments));
            }

            return users;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}