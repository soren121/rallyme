package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
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

@WebServlet(name="AjaxRally", urlPatterns={"/AjaxRally"})
public class AjaxRally extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();

        //don't know where we want to redirect to???????????
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
    	
        // Attempt to get all rallies in database
		Rally[] ralliesArray;
		try {
			ralliesArray = Rally.getAllRallies();
			// Store Rally[] array as object in session
			request.getSession().setAttribute("rallies", ralliesArray);
			
			//don't know where we want to redirect to????
			response.sendRedirect("/Dashboard");
		} catch (UserException ex) {
			ex.printStackTrace();
		}
		
    }

}
