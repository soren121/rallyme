/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.Register
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

/**
    The page provided by this controller allows users to register accounts
    as event organizers.
 */
@WebServlet(name="Register", urlPatterns={"/Register"})
public class Register extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();

        try {
            freemarker.getTemplate("registration.ftl").process(root, response.getWriter());
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
        String firstName= request.getParameter("firstname");
        String lastName= request.getParameter("lastname");
        String email= request.getParameter("email");
        

        try {
            // Attempt to log user in
            User user = User.register(userName, password, email, firstName, lastName);
            // Store User object in session
            request.getSession().setAttribute("user", user);
            // Redirect to dashboard
            response.sendRedirect("Dashboard");
        } catch(UserException ex) {
            Map<String, Object> root = new HashMap<>();
            root.put("error", ex.getMessage());

            try {
                freemarker.getTemplate("registration.ftl").process(root, response.getWriter());
            } catch(TemplateException ex2) {
                throw new RuntimeException(ex2);
            }
        }
    }

}
