package me.kupchenko.plugin.servlet;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Named("pluginConfigServlet")
public class PluginConfigServlet extends BaseSecuredServlet {
    private final TemplateRenderer renderer;

    @Autowired
    public PluginConfigServlet(@ComponentImport UserManager userManager,
                               @ComponentImport TemplateRenderer renderer) {
        super(userManager);
        this.renderer = renderer;
    }

    @Override
    void doGetInternal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> renderContext = new HashMap<>();
        renderContext.put("request", request);

        response.setContentType("text/html;charset=utf-8");
        renderer.render("templates/plugin-config.vm", renderContext, response.getWriter());
    }
}
