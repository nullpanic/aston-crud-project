package dev.nullpanic.crudproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ServletServiceImplTest {
    private static final String JSON = "{\"id\":1}";
    private final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse httpServletResponse = Mockito.mock(HttpServletResponse.class);
    private final BufferedReader bufferedReader = Mockito.mock(BufferedReader.class);
    private final ServletOutputStream servletOutputStream = Mockito.mock(ServletOutputStream.class);
    ServletService servletService;

    @BeforeEach
    public void init() {
        servletService = new ServletServiceImpl();
    }

    @Test
    public void testGetPostBody_shouldExtractBodyFromRequest_WhenInvoked() throws IOException {
        Mockito.when(httpServletRequest.getReader())
                .thenReturn(bufferedReader);

        Mockito.when(bufferedReader.readLine())
                .thenReturn("hello world!")
                .thenReturn(null);

        assertEquals("hello world!", servletService.getPostBody(httpServletRequest));
    }


    @Test
    public void testSendJsonResponse_ShouldSendResponseWith200Code_WhenInvoked() throws IOException {
        Mockito.when(httpServletResponse.getOutputStream())
                        .thenReturn(servletOutputStream);
        Mockito.doNothing().when(servletOutputStream).print(JSON);

        servletService.sendJsonResponse(JSON,httpServletResponse);

        Mockito.verify(httpServletResponse).setStatus(200);
        Mockito.verify(httpServletResponse).getOutputStream();
        Mockito.verify(servletOutputStream).println(JSON);
    }
}