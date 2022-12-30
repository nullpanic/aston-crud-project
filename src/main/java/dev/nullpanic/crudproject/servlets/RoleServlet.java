package dev.nullpanic.crudproject.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import dev.nullpanic.crudproject.configs.DataSource;
import dev.nullpanic.crudproject.dto.RoleDTO;
import dev.nullpanic.crudproject.mappers.RoleMapper;
import dev.nullpanic.crudproject.mappers.RoleMapperImpl;
import dev.nullpanic.crudproject.persist.models.Role;
import dev.nullpanic.crudproject.persist.repositories.RoleDAOImpl;
import dev.nullpanic.crudproject.services.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "UserRolesServlet", value = "/roles/*")
public class RoleServlet extends HttpServlet {
    RoleService roleService;
    ServletService servletService;
    ObjectMapper mapper;
    RoleMapper roleMapper;

    @Override
    public void init() {
        servletService = new ServletServiceImpl();
        roleService = new RoleServiceImpl(new RoleDAOImpl(new HikariDataSource(DataSource.config)));
        mapper = new ObjectMapper();
        roleMapper = new RoleMapperImpl();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            //Get all users
            if (pathInfo == null || "/".equals(pathInfo)) {
                Set<RoleDTO> roles = roleService.getAll()
                        .stream().map(role -> roleMapper.roleToRoleDTO(role))
                        .collect(Collectors.toSet());

                servletService.sendJsonResponse(mapper.writeValueAsString(roles), resp);
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

            Long userId = Long.parseLong(split[1]);

            //Get user by id
            RoleDTO roleDTO = roleMapper.roleToRoleDTO(roleService.get(userId)
                    .orElseThrow());
            servletService.sendJsonResponse(mapper.writeValueAsString(roleDTO), resp);

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        } catch (
                NoSuchElementException exception) {
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
                RoleDTO roleDTO = mapper.readValue(json, RoleDTO.class);
                Role role = roleService.create(roleMapper.roleDTOToRoleWithoutId(roleDTO));
                servletService.sendJsonResponse(mapper.writeValueAsString(roleMapper.roleToRoleDTO(role)), resp);
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
        String json = servletService.getPostBody(req);

        if (json.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

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

        try {
            //update role
            RoleDTO roleDTO = mapper.readValue(json, RoleDTO.class);
            boolean result = roleService.update(roleMapper.roleDTOToRoleWithId(roleDTO, id));

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

        long roleId = Long.parseLong(split[1]);

        try {
            //Delete role
            if (split.length == 2) {
                Role role = roleService.get(roleId).orElseThrow();

                if (!roleService.delete(role)) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Delete failed");
                } else {
                    resp.setStatus(200);
                }
            }

        } catch (SQLException exception) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
        } catch (NoSuchElementException exception) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }
}
