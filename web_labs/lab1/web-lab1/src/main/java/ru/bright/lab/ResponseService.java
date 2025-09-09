package ru.bright.lab;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;

public class ResponseService {

    private static Gson gson = new Gson();


    public void sendJsonResponse(Object response) {
        String jsonResponse = gson.toJson(response);
        System.out.println("Status: 200 OK");
        System.out.println("Content-Type: application/json\n");
        System.out.println(jsonResponse);
    }

    public void sendJsonError(String message) {
        Map<String, String> error = Collections.singletonMap("error", message);
        String jsonError = gson.toJson(error);
        System.out.println("Content-Type: application/json\n");
        System.out.println(jsonError);
    }

}
