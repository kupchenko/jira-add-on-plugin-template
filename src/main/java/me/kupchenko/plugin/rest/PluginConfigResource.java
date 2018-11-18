package me.kupchenko.plugin.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import lombok.extern.slf4j.Slf4j;
import me.kupchenko.plugin.config.SettingsService;
import me.kupchenko.plugin.exception.ConfigSavingException;
import me.kupchenko.plugin.model.ErrorResponse;
import me.kupchenko.plugin.model.PluginConfig;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Slf4j
@Path("/plugin-config")
public class PluginConfigResource {
    private final UserManager userManager;
    private final SettingsService settingsService;
    private final ObjectMapper objectMapper;

    public PluginConfigResource(@ComponentImport UserManager userManager,
                                SettingsService settingsService) {
        this.settingsService = settingsService;
        this.userManager = userManager;
        objectMapper = new ObjectMapper();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPluginConfig(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException {
        UserKey userKey = userManager.getRemoteUserKey(request);
        if (userKey == null || !userManager.isSystemAdmin(userKey)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        log.info("Returning plugin config");

        try {
            PluginConfig config = settingsService.getConfig();
            String jsonResponse = objectMapper.writeValueAsString(config);
            return Response.ok(jsonResponse).build();
        } catch (ConfigSavingException e) {
            ErrorResponse errorResponse = new ErrorResponse("config.retrieving.error", "Error while retrieving plugin config.");
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePluginConfig(final PluginConfig pluginConfig, @Context HttpServletRequest request) throws IOException {
        UserKey userKey = userManager.getRemoteUserKey(request);
        if (userKey == null || !userManager.isSystemAdmin(userKey)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        log.info("Updating plugin config");

        try {
            settingsService.updateConfig(pluginConfig);
        } catch (ConfigSavingException e) {
            ErrorResponse errorResponse = new ErrorResponse("config.saving.error", "Error while saving plugin config.");
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonResponse).build();
        }

        return Response.noContent().build();
    }
}
