package dev.nullpanic.crudproject.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class ServletServiceImpl implements ServletService {
    @Override
    public String getPostBody(HttpServletRequest request) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    @Override
    public void sendJsonResponse(String json, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setHeader("Content-Type", "application/json");
        response.getOutputStream().println(json);
    }


}


