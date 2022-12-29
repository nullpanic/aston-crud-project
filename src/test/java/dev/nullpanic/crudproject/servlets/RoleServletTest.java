package dev.nullpanic.crudproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.mappers.RoleMapperImpl;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.services.RoleService;
import dev.nullpanic.crudproject.services.ServletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleServletTest {

    private RoleServlet roleServlet = Mockito.spy(RoleServlet.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final ServletService servletService = Mockito.mock(ServletService.class);
    private Role role;
    private RoleDTO roleDTO;
    private final Set<Role> roles = new HashSet<>();
    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void init() {
        roleServlet = new RoleServlet();
        roleServlet.roleService = roleService;
        roleServlet.roleMapper = new RoleMapperImpl();
        roleServlet.mapper = new ObjectMapper();
        roleServlet.servletService = servletService;

        role = Role.builder()
                .id(1L)
                .role("Tester")
                .build();

        roleDTO = RoleDTO.builder()
                .id(1L)
                .role("Tester")
                .build();

        roles.add(Role.builder()
                .id(2L)
                .role("Admin")
                .build());
        roles.add(Role.builder()
                .id(3L)
                .role("Programmer")
                .build());
    }

    @Test
    public void testDoGet_WhenNotExistArgs_ShouldInvokeRoleServiceGetAll() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(roleService.getAll())
                .thenReturn(roles);
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(response.getClass()));

        roleServlet.doGet(request, response);

        Mockito.verify(roleService).getAll();
    }

    @Test
    public void testDoGet_WhenAllUsersRequested_ShouldInvokeServletServiceSendJSONResponse() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(roleService.getAll())
                .thenReturn(roles);
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));

        roleServlet.doGet(request, response);

        Mockito.verify(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void testDoGet_WhenMoreTheTwoArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/role/5/test");

        roleServlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_WhenSecondArgNotDigit_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/haha");

        roleServlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_WhenSecondArgIsDigit_ShouldInvokeRoleServiceGet() throws SQLException, IOException {
        Long userId = 2L;
        Mockito.when(request.getPathInfo())
                .thenReturn("/" + userId);
        Mockito.when(roleService.get(userId))
                .thenReturn(Optional.of(role));
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));

        roleServlet.doGet(request, response);

        Mockito.verify(servletService).sendJsonResponse(mapper.writeValueAsString(roleDTO), response);
    }

    @Test
    public void testDoPost_WhenQueryNotExistBody_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("");

        roleServlet.doPost(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WhenNotExistArgs_ShouldInvokeRoleServiceCreate() throws SQLException, IOException {
        role.setId(null);

        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(roleService.create(Mockito.any(Role.class)))
                .thenReturn(role);
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(response.getClass()));

        roleServlet.doPost(request, response);

        Mockito.verify(roleService).create(role);
    }

    @Test
    public void testDoPut_WhenNotExistArgs_ShouldSendBadRequest() throws ServletException, IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn(null);

        roleServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenMoreThenTwoArgs_ShouldSendBadRequest() throws ServletException, IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/test/5/test");

        roleServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenSecondArgsNotDigit_ShouldSendBadRequest() throws ServletException, IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/notDigit");

        roleServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenSecondArgIsDigit_ShouldInvokeRoleServiceUpdate() throws IOException, SQLException {
        long roleId = 1L;

        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/" + roleId);

        roleServlet.doPut(request, response);

        Mockito.verify(roleService).update(role);
    }

    @Test
    public void testDoDelete_WhenNotExistArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);

        roleServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenMoreThenThreeArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/test/4/test");

        roleServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenSecondArgNotDigit_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/notDigit");

        roleServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenSecondArgIsDigit_ShouldInvokeRoleServiceGet() throws SQLException, IOException {
        Long roleId = 1L;

        Mockito.when(request.getPathInfo())
                .thenReturn("/1");
        Mockito.when(roleService.get(roleId))
                .thenReturn(Optional.of(role));

        roleServlet.doDelete(request, response);

        Mockito.verify(roleService).get(roleId);
    }

    @Test
    public void testDoDelete_WhenSecondArgIsDigit_ShouldInvokeRoleServiceDelete() throws SQLException, IOException {
        Long roleId = 1L;

        Mockito.when(request.getPathInfo())
                .thenReturn("/1");
        Mockito.when(roleService.get(roleId))
                .thenReturn(Optional.of(role));

        roleServlet.doDelete(request, response);
        Mockito.verify(roleService).delete(role);
    }
}