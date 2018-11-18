package me.kupchenko.plugin.servlet;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is required because the exposed servlet has no protection.
 */
public abstract class BaseSecuredServlet extends HttpServlet {

    private final UserManager userManager;

    BaseSecuredServlet(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Do not override this implementation. Instead override/use do***Internal()
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (isAdmin(req)) {
            doGetInternal(req, resp);
        } else {
            String message = "You must be an administrator to access this resource.";
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        }
    }

    abstract void doGetInternal(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;

    private boolean isAdmin(HttpServletRequest req) {
        UserKey userKey = userManager.getRemoteUserKey(req);
        return userKey != null && userManager.isSystemAdmin(userKey);
    }
}
