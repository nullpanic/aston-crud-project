package dev.nullpanic.crudproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.dto.UserDTO;
import dev.nullpanic.crudproject.mappers.RoleMapper;
import dev.nullpanic.crudproject.mappers.RoleMapperImpl;
import dev.nullpanic.crudproject.mappers.UserMapper;
import dev.nullpanic.crudproject.mappers.UserMapperImpl;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.models.User;
import dev.nullpanic.crudproject.persist.repositories.RoleDAOImpl;
import dev.nullpanic.crudproject.services.*;
import dev.nullpanic.crudproject.persist.repositories.UserDAOImpl;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "UserServlet", value = "/users/*")
public class UserServlet extends HttpServlet {
    UserService userService;
    RoleService roleService;
    ServletService servletService;
    ObjectMapper mapper;
    UserMapper userMapper;
    RoleMapper roleMapper;

    @Override
    public void init() {
        userService = new UserServiceImpl(new UserDAOImpl());
        servletService = new ServletServiceImpl();
        roleService = new RoleServiceImpl(new RoleDAOImpl());
        mapper = new ObjectMapper();
        userMapper = new UserMapperImpl();
        roleMapper = new RoleMapperImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            //Get all users
            if (pathInfo == null || "/".equals(pathInfo)) {
                Set<UserDTO> users = userService.getAll()
                        .stream().map(user ->
                                UserDTO.builder()
                                        .id(user.getId())
                                        .name(user.getName())
                                        .build())
                        .collect(Collectors.toSet());

                servletService.sendJsonResponse(mapper.writeValueAsString(users), resp);
                return;
            }

            String[] split = pathInfo.split("/");

            //Other cases
            if (split.length > 4) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!StringUtils.isNumeric(split[1])) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Long userId = Long.parseLong(split[1]);

            //Get user by id
            if (split.length == 2) {
                UserDTO userDTO = userMapper.userToUserDTO(userService.getById(userId)
                        .orElseThrow());
                servletService.sendJsonResponse(mapper.writeValueAsString(userDTO), resp);
            }

            //Get all roles
            if (split.length == 3) {
                if ("roles".equals(split[2])) {
                    Set<RoleDTO> roles = userService.getUserRoles(userId)
                            .stream().map(role ->
                                    RoleDTO.builder()
                                            .id(role.getId())
                                            .role(role.getRole())
                                            .build())
                            .collect(Collectors.toSet());

                    servletService.sendJsonResponse(mapper.writeValueAsString(roles), resp);
                    return;
                }
            }

            //Get role by user id
            if (split.length == 4) {

                if (!StringUtils.isNumeric(split[3])) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                Long roleId = Long.parseLong(split[3]);

                if ("roles".equals(split[2])) {
                    Role role = userService.getUserRoleById(userId, roleId).orElseThrow();
                    RoleDTO roleDTO = roleMapper.roleToRoleDTO(role);
                    servletService.sendJsonResponse(mapper.writeValueAsString(roleDTO), resp);
                }
            }

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        } catch (NoSuchElementException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String json = servletService.getPostBody(req);

        if (json.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                UserDTO userDTO = mapper.readValue(json, UserDTO.class);
                User user = userService.create(userMapper.userDTOToUserWithoutId(userDTO));
                servletService.sendJsonResponse(mapper.writeValueAsString(userMapper.userToUserDTO(user)), resp);
                return;
            }

            String[] split = pathInfo.split("/");

            if (split.length > 3) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            if (!StringUtils.isNumeric(split[1])) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            long userId = Long.parseLong(split[1]);

            //Add role to user
            if (split.length == 3) {

                if (!"roles".equals(split[2])) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                RoleDTO roleDTO = mapper.readValue(json, RoleDTO.class);
                User user = userService.getById(userId).orElseThrow();
                Role role = roleService.get(roleDTO.getId()).orElseThrow();

                boolean result = userService.addRoleToUser(user, role);

                if (result) {
                    resp.setStatus(200);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        } catch (NoSuchElementException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] split = pathInfo.split("/");

        if (split.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!StringUtils.isNumeric(split[1])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long id = Long.parseLong(split[1]);
        String json = servletService.getPostBody(req);

        if (json.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            //update user
            UserDTO userDTO = mapper.readValue(json, UserDTO.class);
            boolean result = userService.update(userMapper.userDTOToUserWithId(userDTO, id));

            if (result) {
                resp.setStatus(200);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        String json = servletService.getPostBody(req);

        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] split = pathInfo.split("/");

        if (split.length > 3) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (!StringUtils.isNumeric(split[1])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        long userId = Long.parseLong(split[1]);

        try {
            //Delete user
            if (split.length == 2) {
                if (!userService.delete(userId)) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Delete failed");
                } else {
                    resp.setStatus(200);
                }
                return;
            }

            if (json.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            //Delete user role
            if ("roles".equals(split[2])) {

                RoleDTO roleDTO = mapper.readValue(json, RoleDTO.class);
                User user = userService.getById(userId).orElseThrow();
                Role role = roleService.get(roleDTO.getId()).orElseThrow();

                boolean result = userService.deleteUserRole(user, role);

                if (result) {
                    resp.setStatus(200);
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        } catch (NoSuchElementException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }
}
