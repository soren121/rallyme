/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.Dashboard
 */

package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.User;
import rallyme.exception.RallyException;

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

@WebServlet(name="Dashboard", urlPatterns={"/Dashboard"})
public class Dashboard extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         User user = (User)request.getSession().getAttribute("user");

        Map<String, Object> root = new HashMap<>();
        Rally[] rallies;
        try {
            rallies = Rally.getRalliesByUser(user.getId());
        } catch (RallyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        root.put("rallylist", rallies);
        root.put("user", user);//for getting user firstname
        try {
            freemarker.getTemplate("dashboard.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

}
