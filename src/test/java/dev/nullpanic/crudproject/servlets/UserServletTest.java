package dev.nullpanic.crudproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.dto.UserDTO;
import dev.nullpanic.crudproject.mappers.RoleMapperImpl;
import dev.nullpanic.crudproject.mappers.UserMapperImpl;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;
import dev.nullpanic.crudproject.services.RoleService;
import dev.nullpanic.crudproject.services.ServletService;
import dev.nullpanic.crudproject.services.UserService;
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

class UserServletTest {

    private UserServlet userServlet = Mockito.spy(UserServlet.class);
    private final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    private final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
    private final UserService userService = Mockito.mock(UserService.class);
    private final RoleService roleService = Mockito.mock(RoleService.class);
    private final ServletService servletService = Mockito.mock(ServletService.class);
    private User user;
    private UserDTO userDTO;
    private Role role;
    private RoleDTO roleDTO;
    private final Set<User> users = new HashSet<>();
    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    public void init() {
        userServlet = new UserServlet();
        userServlet.userService = userService;
        userServlet.roleService = roleService;
        userServlet.userMapper = new UserMapperImpl();
        userServlet.roleMapper = new RoleMapperImpl();
        userServlet.mapper = new ObjectMapper();
        userServlet.servletService = servletService;

        user = User.builder()
                .id(1L)
                .name("Cassandra")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .name("Cassandra")
                .build();

        users.add(User.builder()
                .id(2L)
                .name("Leo")
                .build());
        users.add(User.builder()
                .id(3L)
                .name("Jimmy")
                .build());

        role = Role.builder()
                .id(1L)
                .role("Programmer")
                .build();
        roleDTO = RoleDTO.builder()
                .id(1L)
                .role("Programmer")
                .build();

    }

    @Test
    public void testDoGet_WhenNotExistArgs_ShouldInvokeUserServiceGetAll() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(userService.getAll())
                .thenReturn(users);
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));

        userServlet.doGet(request, response);

        Mockito.verify(userService).getAll();
    }

    @Test
    public void testDoGet_WhenNotExistArgs_ShouldInvokeServletServiceSendJSONResponse() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(userService.getAll())
                .thenReturn(users);
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));

        userServlet.doGet(request, response);

        Mockito.verify(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void testDoGet_WhenMoreThenFourArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/role/5/test/4");

        userServlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_WhenSecondArgNotDigit_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/haha");

        userServlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_WhenSecondArgIsDigit_ShouldInvokeUserServiceGetById() throws SQLException, IOException {
        Long userId = 2L;
        Mockito.when(request.getPathInfo())
                .thenReturn("/" + userId);
        Mockito.when(userService.getById(userId))
                .thenReturn(Optional.of(user));
        Mockito.doNothing()
                .when(servletService).sendJsonResponse(Mockito.anyString(), Mockito.any(HttpServletResponse.class));

        userServlet.doGet(request, response);

        Mockito.verify(servletService).sendJsonResponse(mapper.writeValueAsString(userDTO), response);
    }

    @Test
    public void testDoGet_WhenFourthArgIsNotDigit_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/role/test");

        userServlet.doGet(request, response);
        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoGet_WhenFourthArgIsDigit_ShouldInvokeUserServiceGetUserRoleById() throws SQLException, IOException {
        Long userId = 2L;
        Long roleId = 1L;

        Mockito.when(request.getPathInfo())
                .thenReturn("/" + userId + "/roles/" + roleId);
        Mockito.when(userService.getById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userService.getUserRoleById(userId, roleId))
                .thenReturn(Optional.of(role));

        userServlet.doGet(request, response);

        Mockito.verify(userService).getUserRoleById(userId, roleId);
    }

    @Test
    public void testDoGet_WhenFourthArgIsDigit_ShouldInvokeServletServiceSendJsonResponse() throws SQLException, IOException {
        Long userId = 2L;
        Long roleId = 1L;

        Mockito.when(request.getPathInfo())
                .thenReturn("/" + userId + "/roles/" + roleId);
        Mockito.when(userService.getById(userId))
                .thenReturn(Optional.of(user));
        Mockito.when(userService.getUserRoleById(userId, roleId))
                .thenReturn(Optional.of(role));

        userServlet.doGet(request, response);

        Mockito.verify(servletService).sendJsonResponse(mapper.writeValueAsString(roleDTO), response);
    }

    @Test
    public void testDoPost_WhenBodyNotExist_ShouldSendBadRequest() throws IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("");

        userServlet.doPost(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WhenNotExistArgs_ShouldInvokeUserServiceCreate() throws IOException, SQLException {
        user.setId(null);

        Mockito.when(request.getPathInfo())
                .thenReturn(null);
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(userService.create(user))
                .thenReturn(user);

        userServlet.doPost(request, response);

        Mockito.verify(userService).create(user);
    }

    @Test
    public void testDoPost_WhenMoreThenThreeArgs_ShouldSendBadRequest() throws ServletException, IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/test/5/test");

        userServlet.doPost(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WhenSecondArgNotDigit_ShouldSendBadRequest() throws IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/notDigit");

        userServlet.doPost(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WhenThirdArgNotRoles_ShouldSendBadRequest() throws IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"role\":\"Tester\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/1/someText");

        userServlet.doPost(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPost_WhenThirdArgIsRoles_ShouldInvokeUserServiceGetById() throws IOException, SQLException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("""
                        {
                            "id" : 1,
                            "role" : "Programmer"
                        }
                        """);

        Mockito.when(request.getPathInfo())
                .thenReturn("/1/roles");

        userServlet.doPost(request, response);

        Mockito.verify(userService).getById(user.getId());
    }

    @Test
    public void testDoPost_WhenThirdArgIsRoles_ShouldInvokeRoleServiceGet() throws IOException, SQLException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("""
                        {
                            "id" : 1,
                            "role" : "Programmer"
                        }
                        """);

        Mockito.when(request.getPathInfo())
                .thenReturn("/1/roles");
        Mockito.when(userService.getById(user.getId()))
                .thenReturn(Optional.of(user));

        userServlet.doPost(request, response);

        Mockito.verify(roleService).get(role.getId());
    }


    @Test
    public void testDoPut_WhenArgsCountNotEqualsTwo_ShouldSendBadRequest() throws IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/someText");

        userServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenNotExistArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn(null);

        userServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenSecondArgsNotDigit_ShouldSendBadRequest() throws ServletException, IOException {
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/notDigit");

        userServlet.doPut(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoPut_WhenSecondArgIsDigit_ShouldInvokeRoleServiceUpdate() throws IOException, SQLException {
        long roleId = 1L;

        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/" + roleId);

        userServlet.doPut(request, response);

        Mockito.verify(userService).update(user);
    }

    @Test
    public void testDoPut_WhenSecondArgIsDigit_ShouldInvokeUserServiceUpdate() throws IOException, SQLException {
        long roleId = 1L;

        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"name\":\"Cassandra\"}");
        Mockito.when(request.getPathInfo())
                .thenReturn("/" + roleId);

        userServlet.doPut(request, response);

        Mockito.verify(userService).update(user);
    }

    @Test
    public void testDoDelete_WhenNotExistBody_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/1/roles");
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("");

        userServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenNotExistArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn(null);

        userServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenMoreThenThreeArgs_ShouldSendBadRequest() throws IOException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/4/test/4/test");

        userServlet.doDelete(request, response);

        Mockito.verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testDoDelete_WhenHaveOneArgs_ShouldInvokeUserServiceDelete() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/1");

        userServlet.doDelete(request, response);

        Mockito.verify(userService).delete(user.getId());
    }


    @Test
    public void testDoDelete_WhenHaveTwoArgs_ShouldInvokeUserServiceGetById() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/1/roles");
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"id\":\"1\"}");

        userServlet.doDelete(request, response);

        Mockito.verify(userService).getById(user.getId());
    }

    @Test
    public void testDoDelete_WhenHaveTwoArgs_ShouldInvokeRoleServiceGet() throws IOException, SQLException {
        Mockito.when(request.getPathInfo())
                .thenReturn("/1/roles");
        Mockito.when(servletService.getPostBody(request))
                .thenReturn("{\"id\":\"1\"}");
        Mockito.when(userService.getById(user.getId()))
                .thenReturn(Optional.of(user));

        userServlet.doDelete(request, response);

        Mockito.verify(roleService).get(role.getId());
    }

}