package dev.nullpanic.crudproject.services;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ServletService {
    String getPostBody(HttpServletRequest request) throws IOException;

    void sendJsonResponse(String json, HttpServletResponse response) throws IOException;

}
