/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rallyme.controller.EditRally
 */

package rallyme.controller;

import rallyme.core.TemplateServlet;
import rallyme.model.Rally;
import rallyme.model.RallyType;
import rallyme.model.User;
import rallyme.exception.RallyException;
import rallyme.exception.UserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;
import java.sql.Timestamp;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name="EditRally", urlPatterns={"/EditRally"})
public class EditRally extends TemplateServlet {

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> root = new HashMap<>();
        User user = (User) request.getSession().getAttribute("user");
        
        root.put("user", user);
        try {
            freemarker.getTemplate("editrally.ftl").process(root, response.getWriter());
        } catch(TemplateException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    
        if(request.getParameter("rally_id_delete") != null){
        	//Delete Rally Button Pressed
            String rally_id = request.getParameter("rally_id_delete");
            
            try {
				Rally.deleteRally(Integer.parseInt(rally_id));
			} catch (NumberFormatException | RallyException e) {
				e.printStackTrace();
			} 
            
            response.sendRedirect("Dashboard");
        	
        } else {
	        //Edit Rally Button Pressed
        	
    	    Rally rally = null;
            String rally_id = request.getParameter("rally_id");
            Rally[] rallies;
        	
			try {
				rallies = Rally.getAllNationalRallies();
			} catch (RallyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	        
	        try {
				rally = Rally.getRallyById(rally_id);
			} catch (NumberFormatException | RallyException e) {
				e.printStackTrace();
			}
	        
	        Map<String, Object> root = new HashMap<>();
	        root.put("rally", rally);
	        root.put("rallylist", rallies);
	        
	        User user = (User) request.getSession().getAttribute("user");
	        root.put("user", user);//for getting user firstname
	        try {
	            freemarker.getTemplate("editrally.ftl").process(root, response.getWriter());
	        } catch(TemplateException ex) {
	            throw new RuntimeException(ex);
	        }
        
        }
        
    }

}
