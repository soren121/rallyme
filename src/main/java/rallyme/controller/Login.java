/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.Login
 */

package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.User;
import rallyme.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name="Login", urlPatterns={"/Login"})
public class Login extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();

        // If ?logout is specified, invalidate the session
        // If user is already logged in, send them to the dashboard
        String logout = request.getParameter("logout");
        if(logout != null && logout.equals("true")) {
            request.getSession().invalidate();
            response.sendRedirect(".");
            return;
        } else if(request.getSession().getAttribute("user") != null) {
            response.sendRedirect("Dashboard");
            return;
        }

        try {
            freemarker.getTemplate("login.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // Attempt to log user in
            User user = User.login(userName, password);
            // Store User object in session
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60 * 30);
            // Redirect to dashboard
            response.sendRedirect("Dashboard");
        } catch(UserException ex) {
            Map<String, Object> root = new HashMap<>();
            root.put("error", ex.getMessage());

            try {
                freemarker.getTemplate("login.ftl").process(root, response.getWriter());
            } catch(TemplateException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

}
